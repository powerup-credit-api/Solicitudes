package co.crediyacorp.api.rest;

import co.crediyacorp.api.dtos.SolicitudEntradaDto;
import co.crediyacorp.api.mappers.SolicitudMapper;
import co.crediyacorp.usecase.crearsolicitud.excepciones.ValidationException;
import co.crediyacorp.usecase.crearsolicitud.transaction_usecase.ExecuteSolicitudUseCase;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private final ExecuteSolicitudUseCase executeSolicitudUseCase;
    private final SolicitudMapper solicitudMapper;
    private final WebClient webClient;


    public Mono<ServerResponse> listenCrearSolicitud(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(SolicitudEntradaDto.class)
                .flatMap(solicitudEntradaDto ->
                        webClient.get()
                                .uri(uriBuilder -> uriBuilder
                                        .path("/api/v1/validar")
                                        .queryParam("email", solicitudEntradaDto.email())
                                        .queryParam("documentoIdentidad", solicitudEntradaDto.documentoIdentidad())
                                        .build())
                                .retrieve()
                                .onStatus(HttpStatusCode::isError, clientResponse ->
                                        clientResponse.bodyToMono(String.class)
                                                .flatMap(errorMsg -> Mono.error(new ValidationException(errorMsg)))
                                )
                                .bodyToMono(Boolean.class)
                                .filter(Boolean::booleanValue) // solo deja pasar si es true
                                .switchIfEmpty(Mono.error(new RuntimeException("Usuario no válido")))
                                .flatMap(valid -> solicitudMapper.toDomain(solicitudEntradaDto)
                                        .flatMap(executeSolicitudUseCase::executeGuardarSolicitud))
                )
                .flatMap(solicitud -> ServerResponse.ok().bodyValue(solicitud));
    }




}
