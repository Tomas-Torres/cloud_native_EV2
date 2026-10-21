package com.lumina.bff.security;

import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.List;

/**
 * El diagrama de arquitectura del profe define DOS emisores de token
 * distintos que deben ser aceptados por el mismo Gateway:
 *
 *   - Front Clientes -> JWT propio, emitido por ms-usuarios (HS256,
 *     firmado con un secreto compartido).
 *   - Front Admin    -> JWT emitido por Azure AD / Microsoft Entra ID
 *     (RS256, firmado con las claves públicas del tenant).
 *
 * Este decoder mira el claim "iss" (issuer) del token SIN validarlo
 * todavía, decide a cuál de los dos JwtDecoder delegar, y recién ahí
 * se valida firma + expiración + issuer/audience con el decoder correcto.
 * Mirar el issuer sin verificar es seguro: no se confía en nada hasta
 * que el decoder correspondiente valida la firma real.
 */
@Component
public class MultiIssuerJwtDecoder implements JwtDecoder {

    private final JwtDecoder luminaDecoder;
    private final JwtDecoder azureDecoder;
    private final String azureIssuer;

    public MultiIssuerJwtDecoder(
            @Value("${jwt.secret}") String luminaSecret,
            @Value("${azure.tenant-id:}") String azureTenantId,
            @Value("${azure.client-id:}") String azureClientId
    ) {
        // --- Decoder para el JWT propio (mismo secreto que usa ms-usuarios) ---
        SecretKey key = new SecretKeySpec(luminaSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        this.luminaDecoder = NimbusJwtDecoder.withSecretKey(key).build();

        // --- Decoder para el JWT de Azure AD ---
        this.azureIssuer = "https://login.microsoftonline.com/" + azureTenantId + "/v2.0";
        String jwkSetUri = "https://login.microsoftonline.com/" + azureTenantId + "/discovery/v2.0/keys";

        NimbusJwtDecoder azure = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();

        OAuth2TokenValidator<Jwt> issuerValidator = JwtValidators.createDefaultWithIssuer(azureIssuer);
        OAuth2TokenValidator<Jwt> audienceValidator = new AzureAudienceValidator(azureClientId);
        azure.setJwtValidator(new DelegatingOAuth2TokenValidator<>(issuerValidator, audienceValidator));

        this.azureDecoder = azure;
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        String issuer = peekIssuer(token);
        if (azureIssuer.equals(issuer)) {
            return azureDecoder.decode(token);
        }
        // Cualquier otro valor de "iss" (incluido null) se trata como token
        // propio de Lumina, que es el comportamiento que ya existía.
        return luminaDecoder.decode(token);
    }

    private String peekIssuer(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            Object iss = jwt.getJWTClaimsSet().getClaim("iss");
            return iss != null ? iss.toString() : null;
        } catch (ParseException e) {
            throw new BadJwtException("Token JWT malformado", e);
        }
    }

    /** Verifica que el token de Azure venga dirigido a esta API (aud = client id o api://client-id). */
    static class AzureAudienceValidator implements OAuth2TokenValidator<Jwt> {
        private final String expectedClientId;

        AzureAudienceValidator(String expectedClientId) {
            this.expectedClientId = expectedClientId;
        }

        @Override
        public OAuth2TokenValidatorResult validate(Jwt jwt) {
            List<String> audiences = jwt.getAudience();
            boolean ok = audiences != null && (
                    audiences.contains(expectedClientId) ||
                    audiences.contains("api://" + expectedClientId)
            );
            if (ok) {
                return OAuth2TokenValidatorResult.success();
            }
            return OAuth2TokenValidatorResult.failure(
                    new OAuth2Error("invalid_token", "Audience del token de Azure no coincide con esta API", null)
            );
        }
    }
}
