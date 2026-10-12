package cl.edusaldo.auth;

import cl.edusaldo.auth.dto.CrearUsuarioDTO;
import cl.edusaldo.auth.dto.LoginRequestDTO;
import cl.edusaldo.auth.dto.LoginResponseDTO;
import cl.edusaldo.auth.model.EstadoCuenta;
import cl.edusaldo.auth.model.Rol;
import cl.edusaldo.auth.model.Usuario;
import cl.edusaldo.auth.repository.UsuarioRepository;
import cl.edusaldo.auth.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioPrueba;

    @BeforeEach
    void setUp() {
        usuarioPrueba = new Usuario("15.666.777-8", "Antonio", "Jara", "admin@colegio.cl", "hash_secreto_123", Rol.ADMINISTRADOR);
        usuarioPrueba.setId(1L);
        usuarioPrueba.setEstado(EstadoCuenta.ACTIVA);
    }

    @Test
    @DisplayName("1. Bloqueo de cuenta tras cinco intentos consecutivos fallidos (HU10)")
    void testBloqueoTrasCincoIntentosFallidos() {
        when(usuarioRepository.findByRut("15.666.777-8")).thenReturn(Optional.of(usuarioPrueba));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        LoginRequestDTO dtoErroneo = new LoginRequestDTO("15.666.777-8", "clave_incorrecta");

        // 4 intentos fallidos
        for (int i = 1; i <= 4; i++) {
            assertThrows(IllegalArgumentException.class, () -> usuarioService.login(dtoErroneo));
            assertEquals(i, usuarioPrueba.getIntentosFallidos());
            assertEquals(EstadoCuenta.ACTIVA, usuarioPrueba.getEstado());
        }

        // 5to intento fallido -> debe bloquear
        assertThrows(IllegalStateException.class, () -> usuarioService.login(dtoErroneo));
        assertEquals(5, usuarioPrueba.getIntentosFallidos());
        assertEquals(EstadoCuenta.BLOQUEADA, usuarioPrueba.getEstado());

        // 6to intento -> rechazado inmediatamente por cuenta bloqueada
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> usuarioService.login(dtoErroneo));
        assertTrue(ex.getMessage().contains("bloqueada"));
    }

    @Test
    @DisplayName("2. Login exitoso resetea contador de intentos fallidos y actualiza último acceso (HU06)")
    void testLoginExitosoReseteaIntentos() {
        usuarioPrueba.setIntentosFallidos(3);
        when(usuarioRepository.findByRut("15.666.777-8")).thenReturn(Optional.of(usuarioPrueba));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        LoginRequestDTO dtoCorrecto = new LoginRequestDTO("15.666.777-8", "hash_secreto_123");
        LoginResponseDTO res = usuarioService.login(dtoCorrecto);

        assertNotNull(res.getToken());
        assertEquals(0, usuarioPrueba.getIntentosFallidos());
        assertNotNull(usuarioPrueba.getUltimoAcceso());
    }

    @Test
    @DisplayName("3. Creación de nuevo usuario inicia en estado PRIMER_ACCESO con clave temporal (HU05)")
    void testCrearUsuarioNuevoQuedaPrimerAcceso() {
        when(usuarioRepository.existsByRut("11.222.333-4")).thenReturn(false);
        when(usuarioRepository.existsByCorreo("marta@colegio.cl")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> {
            Usuario u = i.getArgument(0);
            u.setId(2L);
            return u;
        });

        CrearUsuarioDTO dto = new CrearUsuarioDTO();
        dto.setRut("11.222.333-4");
        dto.setPrimerNombre("Marta");
        dto.setPrimerApellido("Gómez");
        dto.setCorreo("marta@colegio.cl");
        dto.setRol(Rol.ENCARGADO);

        Usuario creado = usuarioService.crearUsuario(dto);

        assertNotNull(creado.getId());
        assertEquals(EstadoCuenta.PRIMER_ACCESO, creado.getEstado());
        assertTrue(creado.getPasswordHash().startsWith("Edu"));
    }

    @Test
    @DisplayName("4. Inactivación y reactivación lógica de usuario preserva el historial (HU13)")
    void testInactivarYReactivarUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioPrueba));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        usuarioService.inactivar(1L);
        assertEquals(EstadoCuenta.INACTIVA, usuarioPrueba.getEstado());

        usuarioService.reactivar(1L);
        assertEquals(EstadoCuenta.ACTIVA, usuarioPrueba.getEstado());
    }
}
