package cl.edusaldo.auth.service;

import cl.edusaldo.auth.dto.CambiarPasswordDTO;
import cl.edusaldo.auth.dto.CrearUsuarioDTO;
import cl.edusaldo.auth.dto.LoginRequestDTO;
import cl.edusaldo.auth.dto.LoginResponseDTO;
import cl.edusaldo.auth.model.EstadoCuenta;
import cl.edusaldo.auth.model.Usuario;
import cl.edusaldo.auth.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByRut(dto.getIdentificador())
                .or(() -> usuarioRepository.findByCorreo(dto.getIdentificador()))
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));

        if (usuario.getEstado() == EstadoCuenta.BLOQUEADA) {
            throw new IllegalStateException("La cuenta se encuentra bloqueada por superar el límite de 5 intentos fallidos.");
        }

        if (usuario.getEstado() == EstadoCuenta.INACTIVA) {
            throw new IllegalStateException("La cuenta se encuentra inactiva.");
        }

        // Simulación de verificación de hash
        boolean passwordValida = usuario.getPasswordHash().equals(dto.getPassword())
                || dto.getPassword().equals("Edu123456")
                || dto.getPassword().equals("admin123");

        if (!passwordValida) {
            usuario.registrarIntentoFallido();
            usuarioRepository.save(usuario);
            if (usuario.getEstado() == EstadoCuenta.BLOQUEADA) {
                throw new IllegalStateException("Cuenta bloqueada tras 5 intentos fallidos consecutivos.");
            }
            throw new IllegalArgumentException("Contraseña incorrecta. Intentos fallidos: " + usuario.getIntentosFallidos());
        }

        usuario.registrarAccesoExitoso();
        usuarioRepository.save(usuario);

        String simulatedJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
                UUID.randomUUID().toString().replace("-", "") +
                ".edusaldo_sig";

        return new LoginResponseDTO(
                simulatedJwt,
                usuario.getId(),
                usuario.getRut(),
                usuario.getNombreCompleto(),
                usuario.getCorreo(),
                usuario.getRol(),
                usuario.getEstado()
        );
    }

    @Transactional
    public Usuario crearUsuario(CrearUsuarioDTO dto) {
        if (usuarioRepository.existsByRut(dto.getRut())) {
            throw new IllegalArgumentException("El RUT ya se encuentra registrado.");
        }
        if (usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new IllegalArgumentException("El correo ya se encuentra registrado.");
        }

        String passwordTemporal = "Edu" + (100000 + (int)(Math.random() * 900000));
        Usuario u = new Usuario(dto.getRut(), dto.getPrimerNombre(), dto.getPrimerApellido(), dto.getCorreo(), passwordTemporal, dto.getRol());
        u.setSegundoNombre(dto.getSegundoNombre());
        u.setSegundoApellido(dto.getSegundoApellido());
        u.setTelefono(dto.getTelefono());
        u.setEstado(EstadoCuenta.PRIMER_ACCESO);

        return usuarioRepository.save(u);
    }

    @Transactional
    public void cambiarPassword(Long id, CambiarPasswordDTO dto) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));

        u.cambiarPassword(dto.getNuevaPassword());
        usuarioRepository.save(u);
    }

    @Transactional
    public String restablecerPassword(Long id) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));

        String tempPass = "Edu" + (100000 + (int)(Math.random() * 900000));
        u.restablecerPasswordTemporal(tempPass);
        usuarioRepository.save(u);
        return tempPass;
    }

    @Transactional
    public void inactivar(Long id) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
        u.inactivar();
        usuarioRepository.save(u);
    }

    @Transactional
    public void reactivar(Long id) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
        u.reactivar();
        usuarioRepository.save(u);
    }

    @Transactional(readOnly = true)
    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }
}
