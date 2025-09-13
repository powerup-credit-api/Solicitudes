package co.crediyacorp.api.rest;

import co.crediyacorp.api.dtos.DatosTransformacion;
import co.crediyacorp.api.dtos.ValidacionAutomaticaSalidaDto;
import co.crediyacorp.model.external_services.ExternalPusbliser;
import co.crediyacorp.model.solicitud.Solicitud;
import co.crediyacorp.model.solicitud.SolicitudPendienteDto;
import co.crediyacorp.api.dtos.SolicitudEntradaDto;
import co.crediyacorp.api.mappers.SolicitudMapper;
import co.crediyacorp.model.excepciones.ValidationException;


import co.crediyacorp.usecase.crearsolicitud.external_service_use_cases.UsuarioExternalApiPortUseCase;
import co.crediyacorp.usecase.crearsolicitud.transaction_usecase.ExecuteSolicitudUseCase;
import co.crediyacorp.usecase.crearsolicitud.usecases.SolicitudUseCase;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;


import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.math.BigDecimal;



import java.util.Objects;


@Component
@RequiredArgsConstructor
public class Handler {

    private final ExecuteSolicitudUseCase executeSolicitudUseCase;
    private final SolicitudMapper solicitudMapper;
    private final UsuarioExternalApiPortUseCase usuarioExternalApiPortUseCase;
    private final SolicitudUseCase solicitudUseCase;
    private final ExternalPusbliser externalPusbliser;



    public Mono<ServerResponse> listenObtenerSolicitudesPendientes(ServerRequest request) {
        Flux<SolicitudPendienteDto> solicitudesConSalarios = solicitudUseCase.obtenerSolicitudesPendientes(
                        request.queryParam("page").map(Integer::parseInt).orElse(0),
                        request.queryParam("size").map(Integer::parseInt).orElse(50),
                        request.queryParam("monto").map(BigDecimal::new).orElse(null),
                        request.queryParam("sortDirection").orElse("ASC")
                        ,request.queryParam("estado").orElse(null)
                );

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(solicitudesConSalarios, SolicitudPendienteDto.class);
    }

    public Mono<ServerResponse> listenActualizarEstadoPeticion(ServerRequest request) {
        String idSolicitud =  request.queryParam("idSolicitud").orElseThrow(()-> new ValidationException("El id de la solicitud es obligatorio"));
        String estado = request.queryParam("nuevoEstado").orElseThrow(() -> new ValidationException("El nuevo estado es obligatorio"));

        return executeSolicitudUseCase.executeActualizarSolicitud(
                        idSolicitud,
                        estado
                )
                .flatMap(solicitudMapper::toResponse)
                .flatMap(solicitud ->

                        externalPusbliser.enviarEmail(solicitud)
                                .then(ServerResponse.ok().bodyValue(solicitud))
                );


    }

    public Mono<ServerResponse> listenCrearSolicitud(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(SolicitudEntradaDto.class)
                .flatMap(dto -> validarEmailHeader(serverRequest, dto))
                .flatMap(this::validarUsuario)
                .flatMap(solicitudMapper::toDomain)
                .flatMap(executeSolicitudUseCase::executeGuardarSolicitud)
                .flatMap(solicitud ->
                        solicitudUseCase.tieneValidacionAutomatica(solicitud.getIdTipoPrestamo())
                                .flatMap(shouldSend ->
                                        Boolean.TRUE.equals(shouldSend)
                                                ? validacionAutomaticaEnviar(solicitud).thenReturn(solicitud)
                                                : Mono.just(solicitud)
                                )
                )
                .flatMap(solicitud -> ServerResponse.ok().bodyValue(solicitud));
    }


    private Mono<SolicitudEntradaDto> validarEmailHeader(ServerRequest request, SolicitudEntradaDto dto) {
        return Objects.equals(request.headers().firstHeader("X-USER-SUBJECT"), dto.email())
                ? Mono.just(dto)
                : Mono.error(new ValidationException("El email no coincide con el usuario autenticado"));
    }

    private Mono<SolicitudEntradaDto> validarUsuario(SolicitudEntradaDto dto) {
        return usuarioExternalApiPortUseCase.validarUsuario(dto.email(), dto.documentoIdentidad())
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(new ValidationException("Usuario no válido")))
                .thenReturn(dto);
    }

    private Mono<DatosTransformacion> obtenerDatosParaTransformar(Solicitud solicitud) {
        String email = solicitud.getEmail();
        return Mono.zip(
                usuarioExternalApiPortUseCase.consultarSalario(email),
                solicitudUseCase.obtenerSolicitudesPorEstadoAprobado(email).collectList()
        ).map(tuple -> new DatosTransformacion(tuple.getT1(), tuple.getT2()));
    }

    private Mono<ValidacionAutomaticaSalidaDto> mapToSalida(Solicitud solicitud, DatosTransformacion datos) {
        return solicitudMapper.toValidacionAutomaticaSalidaDto(
                solicitud,
                datos.solicitudesAprobadas(),
                datos.salarioBase()
        );
    }

    private Mono<Void> validacionAutomaticaEnviar(Solicitud solicitud) {
        return obtenerDatosParaTransformar(solicitud)
                .flatMap(datos -> mapToSalida(solicitud, datos))
                .flatMap(externalPusbliser::enviarValidacionAutomatica);
    }



}
