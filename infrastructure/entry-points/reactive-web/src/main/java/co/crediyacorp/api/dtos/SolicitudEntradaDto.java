package co.crediyacorp.api.dtos;

import java.math.BigDecimal;


public record SolicitudEntradaDto(
                                  String email,
                                  String documentoIdentidad,
                                  BigDecimal monto,
                                  String plazo,
                                  String tipoPrestamo) {
}
