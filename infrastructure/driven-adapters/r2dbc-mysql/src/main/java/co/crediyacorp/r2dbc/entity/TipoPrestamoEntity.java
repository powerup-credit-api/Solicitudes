package co.crediyacorp.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Table("tipo_prestamo")
public class TipoPrestamoEntity {

    @Id
    @Column("id_tipo_prestamo")
    private String idTipoPrestamo;

    @Column("nombre")
    private String nombre;

    @Column("tasa_interes")
    private BigDecimal tasaInteres;

    @Column("monto_minimo")
    private BigDecimal montoMinimo;

    @Column("monto_maximo")
    private BigDecimal montoMaximo;

    @Column("validacion_automatica")
    private Boolean validacionAutomatica;
}
