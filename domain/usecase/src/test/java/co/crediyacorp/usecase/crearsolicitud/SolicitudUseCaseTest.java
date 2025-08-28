package co.crediyacorp.usecase.crearsolicitud;

import co.crediyacorp.model.estado.gateways.EstadoRepository;
import co.crediyacorp.model.solicitud.Solicitud;
import co.crediyacorp.model.solicitud.gateways.SolicitudRepository;
import co.crediyacorp.model.excepciones.ValidationException;
import co.crediyacorp.usecase.crearsolicitud.usecases.SolicitudUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class SolicitudUseCaseTest {

    @Mock
    private SolicitudRepository solicitudRepository;

    @Mock
    private EstadoRepository estadoRepository;

    @InjectMocks
    private SolicitudUseCase solicitudUseCase;

    private Solicitud buildSolicitudValida() {
        return Solicitud.builder()
                .documentoIdentidad("12345")
                .email("test@email.com")
                .monto(new BigDecimal("1000.00"))
                .plazo("12")
                .idTipoPrestamo("7fa85f64-5717-4562-b3fc-2c963f66afa6")
                .build();
    }

    @Test
    void crearSolicitud_exitoso() {
        Solicitud solicitud = buildSolicitudValida();

        when(estadoRepository.obtenerIdEstadoPorNombre("PENDIENTE_DE_REVISION"))
                .thenReturn(Mono.just("c1f2e110-3a4b-4d2c-8b2f-5f6a7c8d9e10"));
        when(solicitudRepository.guardarSolicitud(any(Solicitud.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(solicitudUseCase.crearSolicitud(solicitud))
                .assertNext(result -> {
                    assert result.getIdSolicitud() != null;
                    assert result.getFechaCreacion().equals(LocalDate.now());
                    assert result.getIdEstado().equals("c1f2e110-3a4b-4d2c-8b2f-5f6a7c8d9e10");
                    assert result.getIdTipoPrestamo().equals("7fa85f64-5717-4562-b3fc-2c963f66afa6");
                })
                .verifyComplete();

        verify(estadoRepository).obtenerIdEstadoPorNombre("PENDIENTE_DE_REVISION");
        verify(solicitudRepository).guardarSolicitud(any(Solicitud.class));
    }

    @Test
    void crearSolicitud_errorPorCampoVacio() {
        when(estadoRepository.obtenerIdEstadoPorNombre(anyString()))
                .thenReturn(Mono.just("c1f2e110-3a4b-4d2c-8b2f-5f6a7c8d9e10"));

        Solicitud solicitud = buildSolicitudValida().toBuilder()
                .documentoIdentidad(null)
                .build();

        StepVerifier.create(solicitudUseCase.crearSolicitud(solicitud))
                .expectErrorMatches(throwable ->
                        throwable instanceof ValidationException &&
                                throwable.getMessage().equals("El documento de identidad no puede estar vacio")
                )
                .verify();

        verifyNoInteractions(solicitudRepository);
    }
    @Test
    void crearSolicitud_errorEnGuardarSolicitud() {
        Solicitud solicitud = buildSolicitudValida();

        when(estadoRepository.obtenerIdEstadoPorNombre("PENDIENTE_DE_REVISION"))
                .thenReturn(Mono.just("c1f2e110-3a4b-4d2c-8b2f-5f6a7c8d9e10"));
        when(solicitudRepository.guardarSolicitud(any(Solicitud.class)))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(solicitudUseCase.crearSolicitud(solicitud))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("DB error")
                )
                .verify();

        verify(estadoRepository).obtenerIdEstadoPorNombre("PENDIENTE_DE_REVISION");
        verify(solicitudRepository).guardarSolicitud(any(Solicitud.class));
    }
}