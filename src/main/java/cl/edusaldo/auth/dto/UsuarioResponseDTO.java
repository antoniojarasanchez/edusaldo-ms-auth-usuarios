package cl.edusaldo.auth.dto;

import cl.edusaldo.auth.model.EstadoCuenta;
import cl.edusaldo.auth.model.Rol;
import cl.edusaldo.auth.model.Usuario;

public class UsuarioResponseDTO {
    private Long id;
    private String rut;
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;
    private String nombreCompleto;
    private String correo;
    private String telefono;
    private Rol rol;
    private EstadoCuenta estado;

    public UsuarioResponseDTO() {}

    public UsuarioResponseDTO(Usuario u) {
        this.id = u.getId();
        this.rut = u.getRut();
        this.primerNombre = u.getPrimerNombre();
        this.segundoNombre = u.getSegundoNombre();
        this.primerApellido = u.getPrimerApellido();
        this.segundoApellido = u.getSegundoApellido();
        this.nombreCompleto = u.getNombreCompleto();
        this.correo = u.getCorreo();
        this.telefono = u.getTelefono();
        this.rol = u.getRol();
        this.estado = u.getEstado();
    }

    public Long getId() { return id; }
    public String getRut() { return rut; }
    public String getPrimerNombre() { return primerNombre; }
    public String getSegundoNombre() { return segundoNombre; }
    public String getPrimerApellido() { return primerApellido; }
    public String getSegundoApellido() { return segundoApellido; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getCorreo() { return correo; }
    public String getTelefono() { return telefono; }
    public Rol getRol() { return rol; }
    public EstadoCuenta getEstado() { return estado; }
}
