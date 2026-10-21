package com.lumina.bff.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Unifica dos formas distintas de expresar el rol del usuario en el JWT:
 *
 *   - Token propio (ms-usuarios): claim "rol", un solo string, ej "ADMIN".
 *   - Token de Azure AD: claim "roles" (App Roles configurados en el App
 *     Registration), una lista, ej ["Admin"].
 *
 * Ambos terminan como GrantedAuthority "ROLE_ADMIN", para que el resto del
 * Gateway (o de los microservicios, si migran a validar el JWT ellos
 * mismos) no tenga que saber de cuál de los dos emisores vino el token.
 */
@Component
public class JwtRoleConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        String rol = jwt.getClaimAsString("rol");
        if (rol != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + rol.toUpperCase()));
        }

        List<String> azureRoles = jwt.getClaimAsStringList("roles");
        if (azureRoles != null) {
            azureRoles.forEach(r -> authorities.add(new SimpleGrantedAuthority("ROLE_" + r.toUpperCase())));
        }

        return new JwtAuthenticationToken(jwt, authorities);
    }
}
