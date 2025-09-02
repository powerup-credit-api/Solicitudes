package co.crediyacorp.api.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RespuestaDto(String idSolicitud,
                           String documentoIdentidad,
                           String email,
                           LocalDate fechaCreacion,
                           BigDecimal monto,
                           String plazo,
                           String tipoPrestamo,
                           String estado) {
}
