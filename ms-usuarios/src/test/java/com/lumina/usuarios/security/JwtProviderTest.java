package com.lumina.usuarios.security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class JwtProviderTest {

    @InjectMocks
    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtProvider, "secret", "testSecretKeyThatIsLongEnoughForHS256Algorithm");
        ReflectionTestUtils.setField(jwtProvider, "expiration", 3600000L); // 1 hora
    }

    @Test
    void generateToken_ConDatosValidos_RetornaToken() {
        String token = jwtProvider.generateToken(1L, "juan@example.com", "CLIENTE");

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
    }

    @Test
    void generateToken_VerificaEstructuraToken() {
        String token = jwtProvider.generateToken(1L, "juan@example.com", "CLIENTE");

        // El token JWT tiene 3 partes separadas por puntos
        String[] parts = token.split("\\.");
        assertThat(parts).hasSize(3);
    }

    @Test
    void getUserIdFromToken_ConTokenValido_RetornaUserId() {
        String token = jwtProvider.generateToken(1L, "juan@example.com", "CLIENTE");

        Long userId = jwtProvider.getUserIdFromToken(token);

        assertThat(userId).isEqualTo(1L);
    }

    @Test
    void getUserIdFromToken_ConTokenInvalido_LanzaExcepcion() {
        String invalidToken = "invalid.token.here";

        assertThatThrownBy(() -> jwtProvider.getUserIdFromToken(invalidToken))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void validateToken_ConTokenValido_RetornaTrue() {
        String token = jwtProvider.generateToken(1L, "juan@example.com", "CLIENTE");

        boolean isValid = jwtProvider.validateToken(token);

        assertThat(isValid).isTrue();
    }

    @Test
    void validateToken_ConTokenInvalido_RetornaFalse() {
        String invalidToken = "invalid.token.here";

        boolean isValid = jwtProvider.validateToken(invalidToken);

        assertThat(isValid).isFalse();
    }

    @Test
    void validateToken_ConTokenNull_RetornaFalse() {
        boolean isValid = jwtProvider.validateToken(null);

        assertThat(isValid).isFalse();
    }

    @Test
    void validateToken_ConTokenVacio_RetornaFalse() {
        boolean isValid = jwtProvider.validateToken("");

        assertThat(isValid).isFalse();
    }
}
