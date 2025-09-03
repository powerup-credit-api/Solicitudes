package co.crediyacorp.api.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SolicitudPendienteDto(String idSolicitud,
                                    String documentoIdentidad,
                                    String email,
                                    LocalDate fechaCreacion,
                                    BigDecimal monto,
                                    BigDecimal salarioBase,
                                    BigDecimal tasaInteres,
                                    String plazo,
                                    String tipoPrestamo,
                                    String estado,
                                    BigDecimal deudaMensualAprobadas) {
}
