package co.crediyacorp.model.solicitud.gateways;

import co.crediyacorp.model.solicitud.Solicitud;
import reactor.core.publisher.Mono;

public interface SolicitudRepository {

    Mono<Solicitud> guardarSolicitud(Solicitud solicitud);
}
