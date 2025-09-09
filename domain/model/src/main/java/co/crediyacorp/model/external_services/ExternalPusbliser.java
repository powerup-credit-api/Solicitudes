package co.crediyacorp.model.external_services;

import reactor.core.publisher.Mono;

public interface ExternalPusbliser {
    Mono<Void> enviar(Object evento);
}
