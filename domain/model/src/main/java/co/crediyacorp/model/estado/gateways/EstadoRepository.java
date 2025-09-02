package co.crediyacorp.model.estado.gateways;

import reactor.core.publisher.Mono;

public interface EstadoRepository {

    Mono<String> obtenerIdEstadoPorNombre(String nombre);
    Mono<String> obtenerNombreEstadoPorId(String id);
}
