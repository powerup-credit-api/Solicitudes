package co.crediyacorp.usecase.crearsolicitud.external_service_use_cases;

import co.crediyacorp.model.external_services.UsuarioExternalApiPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
public class UsuarioExternalApiPortUseCase {

    private final UsuarioExternalApiPort usuarioExternalApiPort;

    public Mono<Boolean> validarUsuario(String email, String documentoIdentidad) {
        return usuarioExternalApiPort.validarUsuario(email, documentoIdentidad);
    }

    public Mono<List<BigDecimal>> consultarSalarios(List<String> empleadosIds) {
        return usuarioExternalApiPort.consultarSalarios(empleadosIds);
    }

    public Mono<BigDecimal> consultarSalario(String email) {
        return usuarioExternalApiPort.consultarSalario(email);
    }







}
