package co.crediyacorp.r2dbc.gateways.ports;

import co.crediyacorp.r2dbc.entity.TipoPrestamoEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Repository
public interface TipoPrestamoR2DBCRepository extends ReactiveCrudRepository<TipoPrestamoEntity, String>, ReactiveQueryByExampleExecutor<TipoPrestamoEntity> {

    Mono<String> findIdByNombre(String nombre);
    Mono<Boolean> existsByNombre(String nombre);
    Mono<TipoPrestamoEntity> findByIdTipoPrestamoAndValidacionAutomaticaFalse(String idTipoPrestamo);

    @Query("SELECT nombre FROM tipo_prestamo WHERE id_tipo_prestamo = :idTipoPrestamo")
    Mono<String> findNombreByIdTipoPrestamo(String idTipoPrestamo);

    @Query("SELECT tasa_interes FROM tipo_prestamo WHERE id_tipo_prestamo = :idTipoPrestamo")
    Mono<BigDecimal> findTasaInteresByIdTipoPrestamo(String idTipoPrestamo);



}
