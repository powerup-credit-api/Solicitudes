package co.crediyacorp.r2dbc.gateways.adapters;

import co.crediyacorp.model.tipoprestamo.TipoPrestamo;
import co.crediyacorp.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.crediyacorp.r2dbc.entity.TipoPrestamoEntity;
import co.crediyacorp.r2dbc.gateways.ports.TipoPrestamoR2DBCRepository;
import co.crediyacorp.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TipoPrestamoRepositoryAdapter extends ReactiveAdapterOperations<
        TipoPrestamo,
        TipoPrestamoEntity,
    String,
        TipoPrestamoR2DBCRepository
> implements TipoPrestamoRepository {


    private final Map<String, Mono<String>>tipoPrestamoCacheIdNombre;
    private final Map<String, Mono<BigDecimal>>tasaInteresCacheIdTipoPrestamo;
    public TipoPrestamoRepositoryAdapter(TipoPrestamoR2DBCRepository repository, ObjectMapper mapper) {

        super(repository, mapper, d -> mapper.map(d, TipoPrestamo.class));
        tipoPrestamoCacheIdNombre= new ConcurrentHashMap<>();
        tasaInteresCacheIdTipoPrestamo= new ConcurrentHashMap<>();
    }

    @Override
    public Mono<String> obtenerIdTipoPrestamoPorNombre(String id) {
        return tipoPrestamoCacheIdNombre.computeIfAbsent(id,key ->
                repository.findIdByNombre(key).cache(Duration.of(10, ChronoUnit.MINUTES))
        );
    }

    @Override
    public Mono<Boolean> existeTipoPrestamoPorNombre(String nombre) {

        return repository.existsByNombre(nombre);
    }

    @Override
    public Mono<Boolean> tieneValidacionManual(String idTipoPrestamo) {
        return repository.findByIdTipoPrestamoAndValidacionAutomaticaFalse(idTipoPrestamo)
                .hasElement();
    }

    @Override
    public Mono<String> obtenerNombreTipoPrestamoPorId(String idTipoPrestamo) {
        return tipoPrestamoCacheIdNombre.computeIfAbsent(idTipoPrestamo,key ->
            repository.findNombreByIdTipoPrestamo(key).cache(Duration.of(10, ChronoUnit.MINUTES))
        );
    }

    @Override
    public Mono<BigDecimal> obtenerTasaInteresPorIdTipoPrestamo(String idTipoPrestamo) {
        return tasaInteresCacheIdTipoPrestamo.computeIfAbsent(idTipoPrestamo,key ->
            repository.findTasaInteresByIdTipoPrestamo(key).cache(Duration.of(10, ChronoUnit.MINUTES))
        );
    }

    @Override
    public Mono<Boolean> tieneValidacionAutomatica(String idTipoPrestamo) {
        return repository.findValidacionAutomaticaByIdTipoPrestamo(idTipoPrestamo);

    }


}
