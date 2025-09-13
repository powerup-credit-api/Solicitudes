package co.crediyacorp.r2dbc.gateways.adapters;

import co.crediyacorp.model.excepciones.ValidationException;
import co.crediyacorp.model.external_services.UsuarioExternalApiPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Component
public class UsuarioUsuarioExternalApiAdapter implements UsuarioExternalApiPort {


    private final WebClient.Builder webClientBuilder;

    @Value("${external.api.usuario.base-url}")
    private String baseUrl ;


    public UsuarioUsuarioExternalApiAdapter(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;

    }


    @Override
    public Mono<Boolean> validarUsuario(String email, String documentoIdentidad) {
        return webClientBuilder
                .baseUrl(baseUrl)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/validar")
                        .queryParam("email", email)
                        .queryParam("documentoIdentidad", documentoIdentidad)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(this::extraerMensaje)
                                .flatMap(msg -> Mono.error(new ValidationException(msg)))
                )
                .bodyToMono(Boolean.class)
                .filter(Boolean::booleanValue);
    }



    @Override
    public Mono<List<BigDecimal>> consultarSalarios(List<String> emails) {
        return webClientBuilder
                .baseUrl(baseUrl)
                .build()
                .post()
                .uri("/api/v1/salario")
                .bodyValue(emails)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(this::extraerMensaje)
                                .flatMap(msg -> Mono.error(new ValidationException(msg)))
                )
                .bodyToMono(new ParameterizedTypeReference<List<BigDecimal>>() {})
                .defaultIfEmpty(Collections.emptyList());
    }

    @Override
    public Mono<BigDecimal> consultarSalario(String email) {
        return webClientBuilder
                .baseUrl(baseUrl)
                .build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/salario")
                        .queryParam("email", email)
                        .build()
                )
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(this::extraerMensaje)
                                .flatMap(msg -> Mono.error(new ValidationException(msg)))
                )
                .bodyToMono(BigDecimal.class)
                .defaultIfEmpty(BigDecimal.ZERO);
    }


    private Mono<String> extraerMensaje(String rawJson) {
        return Mono.justOrEmpty(rawJson)
                .flatMap(json -> extraerCampo(json, "\"message\":\""))
                .filter(msg -> !msg.isBlank())
                .defaultIfEmpty("Error desconocido");
    }


    private Mono<String> extraerCampo(String json, String clave) {
        return Mono.justOrEmpty(json)
                .map(j -> j.indexOf(clave))
                .filter(i -> i >= 0)
                .map(i -> i + clave.length())
                .flatMap(start ->
                        Mono.justOrEmpty(json.indexOf("\"", start))
                                .filter(end -> end > start)
                                .map(end -> json.substring(start, end))
                );
    }




}
