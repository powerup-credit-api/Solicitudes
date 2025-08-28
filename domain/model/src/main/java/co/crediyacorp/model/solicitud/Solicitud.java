package co.crediyacorp.model.solicitud;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Solicitud {

    private String idSolicitud;

    private String documentoIdentidad;

    private String email;

    private LocalDate fechaCreacion;

    private BigDecimal monto;

    private String plazo;

    private String idTipoPrestamo;

    private String idEstado;


}
