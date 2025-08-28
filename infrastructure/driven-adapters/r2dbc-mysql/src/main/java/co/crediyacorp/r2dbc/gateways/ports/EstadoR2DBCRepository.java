package co.crediyacorp.r2dbc.gateways.ports;

import co.crediyacorp.r2dbc.entity.EstadoEnity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


@Repository
public interface EstadoR2DBCRepository extends ReactiveCrudRepository<EstadoEnity, String>, ReactiveQueryByExampleExecutor<EstadoEnity> {

    Mono<String> findIdByNombre(String nombre);
}
