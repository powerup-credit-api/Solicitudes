package co.crediyacorp.api.rest;

import co.crediyacorp.api.dtos.SolicitudEntradaDto;
import co.crediyacorp.api.mappers.SolicitudMapper;
import co.crediyacorp.model.excepciones.ValidationException;
import co.crediyacorp.usecase.crearsolicitud.external_service_use_cases.ExternalApiPortUseCase;
import co.crediyacorp.usecase.crearsolicitud.transaction_usecase.ExecuteSolicitudUseCase;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;


import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
public class Handler {

    private final ExecuteSolicitudUseCase executeSolicitudUseCase;
    private final SolicitudMapper solicitudMapper;
    private final ExternalApiPortUseCase externalApiPortUseCase;


    public Mono<ServerResponse> listenCrearSolicitud(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(SolicitudEntradaDto.class)
                .flatMap(dto -> externalApiPortUseCase.validarUsuario(dto.email(), dto.documentoIdentidad())
                        .filter(Boolean::booleanValue)
                        .switchIfEmpty(Mono.error(new ValidationException("Usuario no válido")))
                        .flatMap(valid -> solicitudMapper.toDomain(dto)
                                .flatMap(executeSolicitudUseCase::executeGuardarSolicitud))
                )
                .flatMap(solicitud -> ServerResponse.ok().bodyValue(solicitud));
    }



}
