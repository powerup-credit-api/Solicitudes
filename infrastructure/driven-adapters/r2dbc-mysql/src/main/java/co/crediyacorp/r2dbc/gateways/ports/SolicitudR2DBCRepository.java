package co.crediyacorp.r2dbc.gateways.ports;

import co.crediyacorp.r2dbc.entity.SolicitudEntity;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository
public interface SolicitudR2DBCRepository extends ReactiveCrudRepository<SolicitudEntity, String>, ReactiveQueryByExampleExecutor<SolicitudEntity> {


        Flux<SolicitudEntity> findByIdEstado(String idEstado);
        Mono<String> findByIdSolicitud(String nombre);

        @Query("SELECT * FROM solicitud WHERE email = :email AND id_estado = :idEstado")
        Flux<SolicitudEntity> findByEmailAndIdEstado(String email, String idEstado);

        }