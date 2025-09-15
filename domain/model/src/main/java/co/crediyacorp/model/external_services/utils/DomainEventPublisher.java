package co.crediyacorp.model.external_services.utils;

import co.crediyacorp.model.solicitud.SolicitudAprobadaEvent;
import reactor.core.publisher.Mono;

public interface DomainEventPublisher {

    Mono<Void> publishSolicitudAprobada(SolicitudAprobadaEvent event);


}
