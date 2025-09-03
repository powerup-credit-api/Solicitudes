package co.crediyacorp.r2dbc.gateways.adapters;

import co.crediyacorp.r2dbc.entity.SolicitudEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class SolicitudFiltersAdapter {

    private final R2dbcEntityTemplate template;

    public Flux<SolicitudEntity> findAllWithFilters(
            List<String> estados,
            Integer page,
            Integer size,
            BigDecimal monto,
            String sortDirection,
            String estadoId
    ) {
        return template.select(SolicitudEntity.class)
                .matching(
                        Query.query(
                                        Stream.of(
                                                        Criteria.where("id_estado").in(estados),
                                                        Optional.ofNullable(monto)
                                                                .map(m -> Criteria.where("monto").is(m))
                                                                .orElse(null),
                                                        (estadoId != null && !estadoId.isBlank() && estados.contains(estadoId))
                                                                ? Criteria.where("id_estado").is(estadoId)
                                                                : null
                                                )
                                                .filter(Objects::nonNull)
                                                .reduce(Criteria::and)
                                                .orElse(Criteria.empty())
                                )
                                .sort("DESC".equalsIgnoreCase(sortDirection)
                                        ? Sort.by("email").descending()
                                        : Sort.by("email").ascending())
                                .limit(size)
                                .offset((long) page * size)
                )
                .all();
    }
}