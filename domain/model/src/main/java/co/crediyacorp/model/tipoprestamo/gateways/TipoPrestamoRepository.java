package co.crediyacorp.model.tipoprestamo.gateways;

import reactor.core.publisher.Mono;

public interface TipoPrestamoRepository {

    Mono<String> obtenerIdTipoPrestamoPorNombre(String id);

    Mono<Boolean> existeTipoPrestamoPorNombre(String nombre);

}
