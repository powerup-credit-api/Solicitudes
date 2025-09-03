package co.crediyacorp.usecase.crearsolicitud.external_service_use_cases;

import co.crediyacorp.model.external_services.ExternalApiPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
public class ExternalApiPortUseCase {

    private final ExternalApiPort externalApiPort;

    public Mono<Boolean> validarUsuario(String email, String documentoIdentidad) {
        return externalApiPort.validarUsuario(email, documentoIdentidad);
    }

    public Mono<List<BigDecimal>> consultarSalarios(List<String> empleadosIds) {
        return externalApiPort.consultarSalarios(empleadosIds);
    }





}
