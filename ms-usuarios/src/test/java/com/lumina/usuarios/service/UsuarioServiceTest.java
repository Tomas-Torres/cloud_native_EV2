package com.lumina.usuarios.service;

import com.lumina.usuarios.entity.Usuario;
import com.lumina.usuarios.entity.Rol;
import com.lumina.usuarios.repository.UsuarioRepository;
import com.lumina.usuarios.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private UsuarioService usuarioService;

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
                .activo(true)
                .build();
    }

    @Test
    void registrarUsuario_ConDatosValidados_RetornaUsuarioCreado() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioRepository.existsByRun(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario resultado = usuarioService.registrar(usuario);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEmail()).isEqualTo("juan@example.com");
        verify(usuarioRepository).existsByEmail("juan@example.com");
        verify(usuarioRepository).existsByRun("12345678-9");
        verify(passwordEncoder).encode("password123");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void registrarUsuario_ConEmailDuplicado_LanzaRuntimeException() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.registrar(usuario))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("El email ya está registrado");

        verify(usuarioRepository).existsByEmail("juan@example.com");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void registrarUsuario_ConRunDuplicado_LanzaRuntimeException() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioRepository.existsByRun(anyString())).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.registrar(usuario))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("El RUN ya está registrado");

        verify(usuarioRepository).existsByEmail("juan@example.com");
        verify(usuarioRepository).existsByRun("12345678-9");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void registrarUsuario_VerificaPasswordEncriptado() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(usuarioRepository.existsByRun(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        usuarioService.registrar(usuario);

        verify(passwordEncoder).encode("password123");
    }

    @Test
    void login_ConCredencialesValidas_RetornaTokenYDatosUsuario() {
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtProvider.generateToken(anyLong(), anyString(), anyString())).thenReturn("jwtToken");

        var resultado = usuarioService.login("juan@example.com", "password123");

        assertThat(resultado).isNotNull();
        assertThat(resultado.get("token")).isEqualTo("jwtToken");
        assertThat(resultado.get("usuario")).isNotNull();
        verify(usuarioRepository).findByEmail("juan@example.com");
        verify(passwordEncoder).matches("password123", usuario.getPassword());
        verify(jwtProvider).generateToken(1L, "juan@example.com", "CLIENTE");
    }

    @Test
    void login_ConEmailNoExistente_LanzaRuntimeException() {
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.login("noexiste@example.com", "password123"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Credenciales incorrectas");

        verify(usuarioRepository).findByEmail("noexiste@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtProvider, never()).generateToken(anyLong(), anyString(), anyString());
    }

    @Test
    void login_ConPasswordIncorrecto_LanzaRuntimeException() {
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThatThrownBy(() -> usuarioService.login("juan@example.com", "wrongPassword"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Credenciales incorrectas");

        verify(usuarioRepository).findByEmail("juan@example.com");
        verify(passwordEncoder).matches("wrongPassword", usuario.getPassword());
        verify(jwtProvider, never()).generateToken(anyLong(), anyString(), anyString());
    }

    @Test
    void obtenerPorId_ConIdValido_RetornaUsuario() {
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.obtenerPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getEmail()).isEqualTo("juan@example.com");
        verify(usuarioRepository).findById(1L);
    }

    @Test
    void obtenerPorId_ConIdNoExistente_LanzaRuntimeException() {
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.obtenerPorId(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuario no encontrado");

        verify(usuarioRepository).findById(999L);
    }
}
