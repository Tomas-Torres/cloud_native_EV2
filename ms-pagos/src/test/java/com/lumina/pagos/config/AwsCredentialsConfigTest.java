package com.lumina.pagos.config;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

import static org.assertj.core.api.Assertions.assertThat;

class AwsCredentialsConfigTest {

    private final AwsCredentialsConfig config = new AwsCredentialsConfig();

    @Test
    void awsCredentialsProvider_DebeCrearCredencialesDeSesion_CuandoHaySessionToken() {
        AwsCredentialsProvider provider = config.awsCredentialsProvider("AKIA-TEST", "secret-test", "token-test");

        assertThat(provider).isInstanceOf(StaticCredentialsProvider.class);

        AwsCredentials credentials = provider.resolveCredentials();
        assertThat(credentials).isInstanceOf(AwsSessionCredentials.class);
        assertThat(credentials.accessKeyId()).isEqualTo("AKIA-TEST");
        assertThat(credentials.secretAccessKey()).isEqualTo("secret-test");
        assertThat(((AwsSessionCredentials) credentials).sessionToken()).isEqualTo("token-test");
    }

    @Test
    void awsCredentialsProvider_DebeCrearCredencialesBasicas_CuandoNoHaySessionToken() {
        AwsCredentialsProvider provider = config.awsCredentialsProvider("AKIA-TEST", "secret-test", "");

        assertThat(provider.resolveCredentials()).isInstanceOf(AwsBasicCredentials.class);
        assertThat(provider.resolveCredentials().accessKeyId()).isEqualTo("AKIA-TEST");
    }

    @Test
    void awsCredentialsProvider_DebeUsarLaCadenaPorDefecto_CuandoNoHayCredenciales() {
        assertThat(config.awsCredentialsProvider("", "", ""))
                .isInstanceOf(DefaultCredentialsProvider.class);

        assertThat(config.awsCredentialsProvider(null, null, null))
                .isInstanceOf(DefaultCredentialsProvider.class);
    }
}
