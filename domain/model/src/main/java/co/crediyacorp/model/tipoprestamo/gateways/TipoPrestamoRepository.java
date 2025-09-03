package co.crediyacorp.model.tipoprestamo.gateways;

import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface TipoPrestamoRepository {

    Mono<String> obtenerIdTipoPrestamoPorNombre(String id);

    Mono<Boolean> existeTipoPrestamoPorNombre(String nombre);
    Mono<Boolean> tieneValidacionManual(String idTipoPrestamo);

    Mono<String> obtenerNombreTipoPrestamoPorId(String idTipoPrestamo);
    Mono<BigDecimal> obtenerTasaInteresPorIdTipoPrestamo(String idTipoPrestamo);
}
