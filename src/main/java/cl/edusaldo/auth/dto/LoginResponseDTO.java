package cl.edusaldo.auth.dto;

import cl.edusaldo.auth.model.EstadoCuenta;
import cl.edusaldo.auth.model.Rol;

public class LoginResponseDTO {
    private String token;
    private Long usuarioId;
    private String rut;
    private String nombreCompleto;
    private String correo;
    private Rol rol;
    private EstadoCuenta estado;

    public LoginResponseDTO() {}

    public LoginResponseDTO(String token, Long usuarioId, String rut, String nombreCompleto, String correo, Rol rol, EstadoCuenta estado) {
        this.token = token;
        this.usuarioId = usuarioId;
        this.rut = rut;
        this.nombreCompleto = nombreCompleto;
        this.correo = correo;
        this.rol = rol;
        this.estado = estado;
    }

    public String getToken() { return token; }
    public Long getUsuarioId() { return usuarioId; }
    public String getRut() { return rut; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getCorreo() { return correo; }
    public Rol getRol() { return rol; }
    public EstadoCuenta getEstado() { return estado; }
}
