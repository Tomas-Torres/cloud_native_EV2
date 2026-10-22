package com.lumina.usuarios.repository;

import com.lumina.usuarios.entity.Usuario;
import com.lumina.usuarios.entity.Rol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioRepositoryTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(1L)
                .nombre("Juan Pérez")
                .email("juan@example.com")
                .password("password123")
                .run("12345678-9")
                .direccion("Calle 123")
                .rol(Rol.CLIENTE)
                .build();
    }

    @Test
    void findByEmail_ConEmailExistente_RetornaUsuario() {
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioRepository.findByEmail("juan@example.com");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getEmail()).isEqualTo("juan@example.com");
    }

    @Test
    void findByEmail_ConEmailNoExistente_RetornaEmpty() {
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioRepository.findByEmail("noexiste@example.com");

        assertThat(resultado).isEmpty();
    }

    @Test
    void findByRun_ConRunExistente_RetornaUsuario() {
        when(usuarioRepository.findByRun(anyString())).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioRepository.findByRun("12345678-9");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getRun()).isEqualTo("12345678-9");
    }

    @Test
    void findByRun_ConRunNoExistente_RetornaEmpty() {
        when(usuarioRepository.findByRun(anyString())).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioRepository.findByRun("98765432-1");

        assertThat(resultado).isEmpty();
    }

    @Test
    void existsByEmail_ConEmailExistente_RetornaTrue() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);

        boolean resultado = usuarioRepository.existsByEmail("juan@example.com");

        assertThat(resultado).isTrue();
    }

    @Test
    void existsByEmail_ConEmailNoExistente_RetornaFalse() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);

        boolean resultado = usuarioRepository.existsByEmail("noexiste@example.com");

        assertThat(resultado).isFalse();
    }

    @Test
    void existsByRun_ConRunExistente_RetornaTrue() {
        when(usuarioRepository.existsByRun(anyString())).thenReturn(true);

        boolean resultado = usuarioRepository.existsByRun("12345678-9");

        assertThat(resultado).isTrue();
    }

    @Test
    void existsByRun_ConRunNoExistente_RetornaFalse() {
        when(usuarioRepository.existsByRun(anyString())).thenReturn(false);

        boolean resultado = usuarioRepository.existsByRun("98765432-1");

        assertThat(resultado).isFalse();
    }
}
