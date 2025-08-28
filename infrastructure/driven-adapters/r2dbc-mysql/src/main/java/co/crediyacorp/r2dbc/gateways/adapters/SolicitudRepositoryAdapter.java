package co.crediyacorp.r2dbc.gateways.adapters;

import co.crediyacorp.model.solicitud.Solicitud;
import co.crediyacorp.model.solicitud.gateways.SolicitudRepository;
import co.crediyacorp.r2dbc.entity.SolicitudEntity;
import co.crediyacorp.r2dbc.gateways.ports.SolicitudR2DBCRepository;
import co.crediyacorp.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class SolicitudRepositoryAdapter extends ReactiveAdapterOperations<
        Solicitud,
        SolicitudEntity,
    String,
        SolicitudR2DBCRepository
> implements SolicitudRepository {
    public SolicitudRepositoryAdapter(SolicitudR2DBCRepository repository, ObjectMapper mapper) {

        super(repository, mapper, d -> mapper.map(d, Solicitud.class));
    }

    @Override
    public Mono<Solicitud> guardarSolicitud(Solicitud solicitud) {
        return super.save(solicitud);
    }
}
