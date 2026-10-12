package cl.edusaldo.auth.controller;

import cl.edusaldo.auth.dto.CambiarPasswordDTO;
import cl.edusaldo.auth.dto.CrearUsuarioDTO;
import cl.edusaldo.auth.dto.UsuarioResponseDTO;
import cl.edusaldo.auth.model.Usuario;
import cl.edusaldo.auth.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Gestión de cuentas y credenciales")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @Operation(summary = "Crear nuevo usuario con clave temporal")
    public ResponseEntity<UsuarioResponseDTO> crear(@Valid @RequestBody CrearUsuarioDTO dto) {
        Usuario u = usuarioService.crearUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UsuarioResponseDTO(u));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar usuario por ID")
    public ResponseEntity<UsuarioResponseDTO> obtenerPorId(@PathVariable Long id) {
        Usuario u = usuarioService.obtenerPorId(id);
        return ResponseEntity.ok(new UsuarioResponseDTO(u));
    }

    @GetMapping
    @Operation(summary = "Listar todos los usuarios registrados")
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        List<UsuarioResponseDTO> list = usuarioService.listarTodos().stream()
                .map(UsuarioResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{id}/password")
    @Operation(summary = "Cambiar voluntariamente la contraseña")
    public ResponseEntity<Map<String, String>> cambiarPassword(
            @PathVariable Long id,
            @Valid @RequestBody CambiarPasswordDTO dto) {
        usuarioService.cambiarPassword(id, dto);
        return ResponseEntity.ok(Map.of("message", "Contraseña actualizada exitosamente"));
    }

    @PostMapping("/{id}/reset-password")
    @Operation(summary = "Restablecer contraseña temporal por administrador")
    public ResponseEntity<Map<String, String>> resetPassword(@PathVariable Long id) {
        String temp = usuarioService.restablecerPassword(id);
        return ResponseEntity.ok(Map.of("tempPassword", temp, "message", "Contraseña temporal generada"));
    }

    @PutMapping("/{id}/inactivar")
    @Operation(summary = "Inactivar cuenta de usuario")
    public ResponseEntity<Map<String, String>> inactivar(@PathVariable Long id) {
        usuarioService.inactivar(id);
        return ResponseEntity.ok(Map.of("message", "Usuario inactivado"));
    }

    @PutMapping("/{id}/reactivar")
    @Operation(summary = "Reactivar cuenta de usuario")
    public ResponseEntity<Map<String, String>> reactivar(@PathVariable Long id) {
        usuarioService.reactivar(id);
        return ResponseEntity.ok(Map.of("message", "Usuario reactivado"));
    }
}
