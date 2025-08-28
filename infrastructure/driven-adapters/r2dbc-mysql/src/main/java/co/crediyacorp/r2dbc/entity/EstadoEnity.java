package co.crediyacorp.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Table("estado")
public class EstadoEnity {

    @Id
    @Column("id_estado")
    private String idEstado;

    @Column("nombre")
    private EstadoEnum nombre;

    @Column("descripcion")
    private String descripcion;
}
