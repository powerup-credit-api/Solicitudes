package co.crediyacorp.api.rest;

import co.crediyacorp.api.dtos.RespuestaDto;
import co.crediyacorp.api.dtos.SolicitudEntradaDto;
import co.crediyacorp.api.mappers.SolicitudMapper;
import co.crediyacorp.model.excepciones.ValidationException;

import co.crediyacorp.usecase.crearsolicitud.external_service_use_cases.ExternalApiPortUseCase;
import co.crediyacorp.usecase.crearsolicitud.transaction_usecase.ExecuteSolicitudUseCase;
import co.crediyacorp.usecase.crearsolicitud.usecases.SolicitudUseCase;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;


import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import java.util.Objects;


@Component
@RequiredArgsConstructor
public class Handler {

    private final ExecuteSolicitudUseCase executeSolicitudUseCase;
    private final SolicitudMapper solicitudMapper;
    private final ExternalApiPortUseCase externalApiPortUseCase;
    private final SolicitudUseCase solicitudUseCase;


    public Mono<ServerResponse> listenCrearSolicitud(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(SolicitudEntradaDto.class)
                .flatMap(dto ->
                        Objects.equals(serverRequest.headers().firstHeader("X-USER-SUBJECT"), dto.email()) ?
                                Mono.just(dto) :
                                Mono.error(new ValidationException("El email no coincide con el usuario autenticado"))

                )
                .flatMap(dto -> externalApiPortUseCase.validarUsuario(dto.email(), dto.documentoIdentidad())
                        .filter(Boolean::booleanValue)
                        .switchIfEmpty(Mono.error(new ValidationException("Usuario no válido")))
                        .flatMap(valid -> solicitudMapper.toDomain(dto)
                                .flatMap(executeSolicitudUseCase::executeGuardarSolicitud))
                )
                .flatMap(solicitud -> ServerResponse.ok().bodyValue(solicitud));
    }

    public Mono<ServerResponse> listenObtenerSolicitudesPendientes(ServerRequest request) {
        return ServerResponse.ok()
                .body(
                        solicitudUseCase.obtenerSolicitudesPendientes(
                                request.queryParam("page").map(Integer::parseInt).orElse(0),
                                request.queryParam("size").map(Integer::parseInt).orElse(50),
                                request.queryParam("monto").map(BigDecimal::new).orElse(null),
                                request.queryParam("sortDirection").orElse("ASC")
                        ).flatMap(solicitudMapper::toResponse),
                        RespuestaDto.class
                );
    }


}
