package co.crediyacorp.api.dtos;


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
        List<SolicitudAprobadaDto> solicitudesAprobadas
) {
}
