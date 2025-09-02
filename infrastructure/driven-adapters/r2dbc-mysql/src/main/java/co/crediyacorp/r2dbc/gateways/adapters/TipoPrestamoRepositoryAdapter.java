package co.crediyacorp.r2dbc.gateways.adapters;

import co.crediyacorp.model.tipoprestamo.TipoPrestamo;
import co.crediyacorp.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.crediyacorp.r2dbc.entity.TipoPrestamoEntity;
import co.crediyacorp.r2dbc.gateways.ports.TipoPrestamoR2DBCRepository;
import co.crediyacorp.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class TipoPrestamoRepositoryAdapter extends ReactiveAdapterOperations<
        TipoPrestamo,
        TipoPrestamoEntity,
    String,
        TipoPrestamoR2DBCRepository
> implements TipoPrestamoRepository {
    public TipoPrestamoRepositoryAdapter(TipoPrestamoR2DBCRepository repository, ObjectMapper mapper) {

        super(repository, mapper, d -> mapper.map(d, TipoPrestamo.class));
    }

    @Override
    public Mono<String> obtenerIdTipoPrestamoPorNombre(String id) {
        return repository.findIdByNombre(id);
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
        return repository.findNombreByIdTipoPrestamo(idTipoPrestamo);
    }


}
