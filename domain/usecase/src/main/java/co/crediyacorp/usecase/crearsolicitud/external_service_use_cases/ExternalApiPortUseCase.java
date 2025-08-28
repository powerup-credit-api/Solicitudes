package co.crediyacorp.usecase.crearsolicitud.external_service_use_cases;

import co.crediyacorp.model.external_services.ExternalApiPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ExternalApiPortUseCase {

    private final ExternalApiPort externalApiPort;

    public Mono<Boolean> validarUsuario(String email, String documentoIdentidad) {
        return externalApiPort.validarUsuario(email, documentoIdentidad);
    }

}
