package com.lumina.usuarios.controller;

import com.lumina.usuarios.entity.Usuario;
import com.lumina.usuarios.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping("/registro")
    public ResponseEntity<Map<String, Object>> registrar(@RequestBody Usuario usuario) {
        Usuario creado = usuarioService.registrar(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "id", creado.getId(),
                "nombre", creado.getNombre(),
                "email", creado.getEmail(),
                "message", "Usuario registrado exitosamente"
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        Map<String, Object> response = usuarioService.login(
                credentials.get("email"),
                credentials.get("password")
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuario(@PathVariable Long id) {
        Usuario usuario = usuarioService.obtenerPorId(id);
        usuario.setPassword(null);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping
    public ResponseEntity<java.util.List<Usuario>> listarUsuarios() {
        java.util.List<Usuario> usuarios = usuarioService.listarTodos();
        usuarios.forEach(u -> u.setPassword(null));
        return ResponseEntity.ok(usuarios);
    }
}
