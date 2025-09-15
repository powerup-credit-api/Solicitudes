package co.crediyacorp.model.solicitud;

import co.crediyacorp.model.external_services.utils.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;

public record SolicitudAprobadaEvent(
        String idSolicitud,
        BigDecimal monto,
        Instant occurredOn
) implements DomainEvent {

    public static SolicitudAprobadaEvent from(Solicitud solicitud) {
        return new SolicitudAprobadaEvent(
                solicitud.getIdSolicitud(),
                solicitud.getMonto(),
                Instant.now()
        );
    }

    @Override
    public String getEventName() {
        return "SolicitudAprobada";
    }

    @Override
    public Instant getOccurredOn() {
        return occurredOn;
    }
}
