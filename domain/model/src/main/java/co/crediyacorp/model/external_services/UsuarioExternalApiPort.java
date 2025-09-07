package co.crediyacorp.model.external_services;

import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

public interface UsuarioExternalApiPort {
    Mono<Boolean> validarUsuario(String email, String documentoIdentidad);

    Mono<List<BigDecimal>> consultarSalarios(List<String> emails);
}
