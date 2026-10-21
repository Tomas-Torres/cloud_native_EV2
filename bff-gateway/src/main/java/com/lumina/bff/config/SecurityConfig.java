package com.lumina.bff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import com.lumina.bff.security.JwtRoleConverter;
import com.lumina.bff.security.MultiIssuerJwtDecoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final MultiIssuerJwtDecoder jwtDecoder;
    private final JwtRoleConverter jwtRoleConverter;

    public SecurityConfig(MultiIssuerJwtDecoder jwtDecoder, JwtRoleConverter jwtRoleConverter) {
        this.jwtDecoder = jwtDecoder;
        this.jwtRoleConverter = jwtRoleConverter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
            .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/api/usuarios/login", "/api/usuarios/registro").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/productos/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/productos/**").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/productos/**").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/productos/**").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/api/bodega/inventario/*/descontar").authenticated()
                .requestMatchers("/api/bodega/**").hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/usuarios", "/api/delivery").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .decoder(jwtDecoder)
                    .jwtAuthenticationConverter(jwtRoleConverter)
                )
            );
        return http.build();
    }
}