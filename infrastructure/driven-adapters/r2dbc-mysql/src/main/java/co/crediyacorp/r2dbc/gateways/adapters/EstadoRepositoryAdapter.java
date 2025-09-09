package co.crediyacorp.r2dbc.gateways.adapters;

import co.crediyacorp.model.estado.Estado;
import co.crediyacorp.model.estado.gateways.EstadoRepository;
import co.crediyacorp.r2dbc.entity.EstadoEntity;
import co.crediyacorp.r2dbc.gateways.ports.EstadoR2DBCRepository;
import co.crediyacorp.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class EstadoRepositoryAdapter extends ReactiveAdapterOperations<
        Estado,
        EstadoEntity,
    String,
        EstadoR2DBCRepository
> implements EstadoRepository {

    private final Map<String, Mono<String>> estadoCacheIdNombre;
    private final Map<String, Mono<String>>estadoCacheNombreId;
    public EstadoRepositoryAdapter(EstadoR2DBCRepository repository, ObjectMapper mapper) {

        super(repository, mapper, d -> mapper.map(d, Estado.class));
        this.estadoCacheIdNombre = new ConcurrentHashMap<>();
        this.estadoCacheNombreId = new ConcurrentHashMap<>();
    }

    @Override
    public Mono<String> obtenerIdEstadoPorNombre(String nombre) {
        return estadoCacheIdNombre.computeIfAbsent(nombre, key->
                repository.findIdEstadoByNombre(key).cache(Duration.of(10, ChronoUnit.MINUTES))
                );
    }

    @Override
    public Mono<String> obtenerNombreEstadoPorId(String id) {
        return estadoCacheNombreId.computeIfAbsent(id,key->
                repository.findNombreByIdEstado(key).cache(Duration.of(10, ChronoUnit.MINUTES))
        );

    }
}
