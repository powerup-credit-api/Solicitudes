package co.crediyacorp.r2dbc.gateways.adapters;

import co.crediyacorp.model.estado.Estado;
import co.crediyacorp.model.estado.gateways.EstadoRepository;
import co.crediyacorp.r2dbc.entity.EstadoEntity;
import co.crediyacorp.r2dbc.gateways.ports.EstadoR2DBCRepository;
import co.crediyacorp.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class EstadoRepositoryAdapter extends ReactiveAdapterOperations<
        Estado,
        EstadoEntity,
    String,
        EstadoR2DBCRepository
> implements EstadoRepository {
    public EstadoRepositoryAdapter(EstadoR2DBCRepository repository, ObjectMapper mapper) {

        super(repository, mapper, d -> mapper.map(d, Estado.class));
    }

    @Override
    public Mono<String> obtenerIdEstadoPorNombre(String nombre) {
        return repository.findIdByNombre(nombre);
    }

    @Override
    public Mono<String> obtenerNombreEstadoPorId(String id) {
        return repository.findNombreByIdEstado(id);
    }
}
