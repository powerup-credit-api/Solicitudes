package co.crediyacorp.model.external_services;

import reactor.core.publisher.Mono;

public interface ExternalPusbliser {
    Mono<Void> enviarEmail(Object evento);

    Mono<Void> enviarValidacionAutomatica(Object evento);
}
