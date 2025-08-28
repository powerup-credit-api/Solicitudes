package co.crediyacorp.model.external_services;

import reactor.core.publisher.Mono;

public interface ExternalApiPort {
    Mono<Boolean> validarUsuario(String email, String documentoIdentidad);
}
