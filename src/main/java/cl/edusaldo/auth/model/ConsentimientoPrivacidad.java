package cl.edusaldo.auth.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "consentimientos_privacidad")
public class ConsentimientoPrivacidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "version_aviso", nullable = false, length = 50)
    private String versionAviso;

    @Column(name = "fecha_aceptacion", nullable = false)
    private LocalDateTime fechaAceptacion = LocalDateTime.now();

    @Column(name = "ip_origen", length = 45)
    private String ipOrigen;

    public ConsentimientoPrivacidad() {
    }

    public ConsentimientoPrivacidad(Usuario usuario, String versionAviso, String ipOrigen) {
        this.usuario = usuario;
        this.versionAviso = versionAviso;
        this.ipOrigen = ipOrigen;
        this.fechaAceptacion = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public String getVersionAviso() { return versionAviso; }
    public LocalDateTime getFechaAceptacion() { return fechaAceptacion; }
    public String getIpOrigen() { return ipOrigen; }
}
