package com.lumina.usuarios;

import com.lumina.usuarios.entity.Rol;
import com.lumina.usuarios.entity.Usuario;
import com.lumina.usuarios.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class MsUsuariosApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsUsuariosApplication.class, args);
    }

    @Bean
    CommandLineRunner crearAdministradorInicial(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            String emailAdmin = "admin@lumina.cl";

            if (!usuarioRepository.existsByEmail(emailAdmin)) {
                Usuario admin = Usuario.builder()
                        .nombre("Administrador")
                        .email(emailAdmin)
                        .password(passwordEncoder.encode("admin123"))
                        .run("11111111-1")
                        .direccion("Santiago")
                        .rol(Rol.ADMIN)
                        .activo(true)
                        .build();

                usuarioRepository.save(admin);

                System.out.println("Administrador inicial creado: " + emailAdmin);
            } else {
                System.out.println("El administrador inicial ya existe.");
            }
        };
    }
}