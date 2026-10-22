package com.lumina.pagos.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

/**
 * Configuracion de credenciales AWS para Spring Cloud AWS.
 *
 * <p>Las credenciales se toman de {@code spring.cloud.aws.credentials.*}, que a su vez leen las
 * variables de entorno {@code AWS_ACCESS_KEY_ID}, {@code AWS_SECRET_ACCESS_KEY} y
 * {@code AWS_SESSION_TOKEN} (ver {@code application.yml}).</p>
 *
 * <p>Este bean es necesario porque Spring Cloud AWS 3.x solo bindea {@code access-key} y
 * {@code secret-key} en {@code CredentialsProperties} y con ellas construye un
 * {@link AwsBasicCredentials} (SIN token de sesion). Las credenciales temporales de AWS Academy
 * requieren {@link AwsSessionCredentials}, por lo que sin este bean las llamadas a SQS fallarian
 * con {@code InvalidClientTokenId}.</p>
 *
 * <p>Al declarar el bean {@link AwsCredentialsProvider}, el auto-configuration oficial
 * ({@code CredentialsProviderAutoConfiguration}) queda desactivado por su
 * {@code @ConditionalOnMissingBean(AwsCredentialsProvider.class)} y no se pisa la configuracion.</p>
 *
 * <p>Si no hay credenciales explicitas se devuelve la cadena por defecto del AWS SDK
 * ({@link DefaultCredentialsProvider}), que resuelve las variables de entorno, el perfil de
 * {@code ~/.aws/credentials} o el IAM role de la instancia.</p>
 */
@Configuration
public class AwsCredentialsConfig {

    /**
     * Provider de credenciales AWS.
     *
     * @param accessKey    clave de acceso (AWS_ACCESS_KEY_ID), vacio si no esta definida
     * @param secretKey    clave secreta (AWS_SECRET_ACCESS_KEY), vacia si no esta definida
     * @param sessionToken token de sesion (AWS_SESSION_TOKEN), vacio si no esta definido
     * @return provider con credenciales de sesion, basicas o la cadena por defecto del SDK
     */
    @Bean
    public AwsCredentialsProvider awsCredentialsProvider(
            @Value("${spring.cloud.aws.credentials.access-key:}") String accessKey,
            @Value("${spring.cloud.aws.credentials.secret-key:}") String secretKey,
            @Value("${spring.cloud.aws.credentials.session-token:}") String sessionToken) {

        if (!StringUtils.hasText(accessKey) || !StringUtils.hasText(secretKey)) {
            // Sin credenciales explicitas -> cadena por defecto del AWS SDK
            // (variables de entorno, perfil local o IAM role de la instancia).
            return DefaultCredentialsProvider.create();
        }

        if (StringUtils.hasText(sessionToken)) {
            // Credenciales temporales (AWS Academy, STS): requieren token de sesion.
            return StaticCredentialsProvider.create(
                    AwsSessionCredentials.create(accessKey, secretKey, sessionToken));
        }

        return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
    }
}
