package co.crediyacorp.api.dtos;

import co.crediyacorp.model.solicitud.Solicitud;

import java.math.BigDecimal;
import java.util.List;

public record ValidacionAutomaticaSalidaDto(
        String idSolicitud,
        String documentoIdentidad,
        String email,
        BigDecimal monto,
        String plazo,
        String tipoPrestamo,
        BigDecimal tasaInteres,
        BigDecimal salarioBase,
        List<Solicitud> solicitudesAprobadas
) {
}
