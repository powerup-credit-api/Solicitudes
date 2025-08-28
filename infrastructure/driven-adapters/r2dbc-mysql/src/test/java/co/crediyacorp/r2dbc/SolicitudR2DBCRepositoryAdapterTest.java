package co.crediyacorp.r2dbc;

import co.crediyacorp.model.solicitud.Solicitud;
import co.crediyacorp.r2dbc.entity.SolicitudEntity;
import co.crediyacorp.r2dbc.gateways.adapters.SolicitudRepositoryAdapter;
import co.crediyacorp.r2dbc.gateways.ports.SolicitudR2DBCRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Example;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SolicitudR2DBCRepositoryAdapterTest {

    @InjectMocks
    SolicitudRepositoryAdapter repositoryAdapter;

    @Mock
    SolicitudR2DBCRepository repository;

    @Mock
    ObjectMapper mapper;


    private SolicitudEntity entity;
    private Solicitud model;

    @BeforeEach
    void setUp() {
        entity = new SolicitudEntity();
        entity.setIdSolicitud("1");
        entity.setDocumentoIdentidad("123456");

        model = new Solicitud();
        model.setIdSolicitud("1");
        model.setDocumentoIdentidad("123456");
    }

    @Test
    void mustFindValueById() {
        when(repository.findById("1")).thenReturn(Mono.just(entity));
        when(mapper.map(any(SolicitudEntity.class), eq(Solicitud.class))).thenReturn(model);

        Mono<Solicitud> result = repositoryAdapter.findById("1");

        StepVerifier.create(result)
                .expectNext(model)
                .verifyComplete();
    }

    @Test
    void mustFindAllValues() {
        when(repository.findAll()).thenReturn(Flux.just(entity));
        when(mapper.map(any(SolicitudEntity.class), eq(Solicitud.class))).thenReturn(model);

        Flux<Solicitud> result = repositoryAdapter.findAll();

        StepVerifier.create(result)
                .expectNext(model)
                .verifyComplete();
    }

    @Test
    void mustFindByExample() {
        when(mapper.map(any(Solicitud.class), eq(SolicitudEntity.class))).thenReturn(entity);
        when(repository.findAll(any(Example.class))).thenReturn(Flux.just(entity));
        when(mapper.map(any(SolicitudEntity.class), eq(Solicitud.class))).thenReturn(model);

        Flux<Solicitud> result = repositoryAdapter.findByExample(model);

        StepVerifier.create(result)
                .expectNext(model)
                .verifyComplete();
    }

    @Test
    void mustSaveValue() {
        when(mapper.map(any(Solicitud.class), eq(SolicitudEntity.class))).thenReturn(entity);
        when(repository.save(any(SolicitudEntity.class))).thenReturn(Mono.just(entity));
        when(mapper.map(any(SolicitudEntity.class), eq(Solicitud.class))).thenReturn(model);

        Mono<Solicitud> result = repositoryAdapter.save(model);

        StepVerifier.create(result)
                .expectNext(model)
                .verifyComplete();
    }
}
