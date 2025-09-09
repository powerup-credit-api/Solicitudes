package co.crediyacorp.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Table("solicitud")
public class SolicitudEntity implements Persistable<String> {

    @Id
    @Column("id_solicitud")
    private String idSolicitud;

    @Column("documento_identidad")
    private String documentoIdentidad;

    @Column("email")
    private String email;

    @Column("fecha_creacion")
    private LocalDate fechaCreacion;

    @Column("monto")
    private BigDecimal monto;

    @Column("plazo")
    private String plazo;

    @Column("id_tipo_prestamo")
    private String idTipoPrestamo;

    @Column("id_estado")
    private String idEstado;


    @Transient
    private boolean isNew = true;

    @Override
    public String getId() {
        return this.idSolicitud;
    }

    @Override
    public boolean isNew() {
        return this.isNew;
    }

    public void markAsPersisted() {
        this.isNew = false;
    }

}
