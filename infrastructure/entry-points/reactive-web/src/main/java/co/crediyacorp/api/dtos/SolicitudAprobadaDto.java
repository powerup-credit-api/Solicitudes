package co.crediyacorp.api.dtos;

import java.math.BigDecimal;

public record SolicitudAprobadaDto(
        String idSolicitud,
        BigDecimal monto,
        String plazo,
        BigDecimal tasaInteres
) { }

