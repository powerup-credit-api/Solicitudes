package co.crediyacorp.r2dbc.gateways.adapters;

import co.crediyacorp.model.solicitud.Solicitud;
import co.crediyacorp.model.solicitud.gateways.SolicitudRepository;
import co.crediyacorp.r2dbc.entity.SolicitudEntity;
import co.crediyacorp.r2dbc.gateways.ports.SolicitudR2DBCRepository;
import co.crediyacorp.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import java.util.List;

@Repository
public class SolicitudRepositoryAdapter extends ReactiveAdapterOperations<
        Solicitud,
        SolicitudEntity,
    String,
        SolicitudR2DBCRepository
> implements SolicitudRepository {


    private final SolicitudFiltersAdapter filtersAdapter;

    public SolicitudRepositoryAdapter(SolicitudR2DBCRepository repository, ObjectMapper mapper, SolicitudFiltersAdapter filtersAdapter) {
        super(repository, mapper, d -> mapper.map(d, Solicitud.class));
        this.filtersAdapter = filtersAdapter;


    }

    @Override
    public Mono<Solicitud> guardarSolicitud(Solicitud solicitud) {
        return super.save(solicitud);
    }

    @Override
    public Flux<Solicitud> obtenerSolicitudesPorEstadoAprobado(String idEstado) {
        return repository.findByIdEstado(idEstado)
                .map(this::toEntity);
    }


    @Override
    public Flux<Solicitud> obtenerSolicitudesPendientes(List<String> estados, Integer page, Integer size, BigDecimal monto, String sortDirection, String estadoId) {
        return filtersAdapter.findAllWithFilters(
                        estados,
                        page,
                        size,
                        monto,
                        sortDirection,
                        estadoId

                )
                .map(this::toEntity);
}

}
