package co.crediyacorp.model.tipoprestamo;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TipoPrestamo {

    private String idTipoPrestamo;

    private String nombre;

    private BigDecimal tasaInteres;

    private BigDecimal montoMinimo;

    private BigDecimal montoMaximo;

    private Boolean validacionAutomatica;

}
