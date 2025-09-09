package co.crediyacorp.r2dbc.gateways.ports;

import co.crediyacorp.r2dbc.entity.EstadoEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


@Repository
public interface EstadoR2DBCRepository extends ReactiveCrudRepository<EstadoEntity, String>, ReactiveQueryByExampleExecutor<EstadoEntity> {

    Mono<String> findIdEstadoByNombre(String nombre);

    @Query("SELECT nombre FROM estado WHERE id_estado = :id")
    Mono<String> findNombreByIdEstado(String id);
}
