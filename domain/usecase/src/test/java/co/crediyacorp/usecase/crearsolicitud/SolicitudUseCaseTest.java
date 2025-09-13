package co.crediyacorp.usecase.crearsolicitud;

import co.crediyacorp.model.estado.gateways.EstadoRepository;
import co.crediyacorp.model.solicitud.Solicitud;
import co.crediyacorp.model.solicitud.SolicitudPendienteDto;
import co.crediyacorp.model.solicitud.gateways.SolicitudRepository;
import co.crediyacorp.model.excepciones.ValidationException;
import co.crediyacorp.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.crediyacorp.usecase.crearsolicitud.external_service_use_cases.UsuarioExternalApiPortUseCase;
import co.crediyacorp.usecase.crearsolicitud.mapper.SolicitudMapperUseCase;
import co.crediyacorp.usecase.crearsolicitud.usecases.SolicitudUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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

    @Mock
    private TipoPrestamoRepository tipoPrestamoRepository;


    @Mock
    private SolicitudMapperUseCase solicitudMapper;

    @Mock
    private UsuarioExternalApiPortUseCase usuarioExternalApiPortUseCase;

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
    void crearSolicitud_exitoso_default() {
        Solicitud solicitud = buildSolicitudValida();

        when(tipoPrestamoRepository.tieneValidacionManual(solicitud.getIdTipoPrestamo()))
                .thenReturn(Mono.just(false));

        when(estadoRepository.obtenerIdEstadoPorNombre("PENDIENTE_DE_REVISION"))
                .thenReturn(Mono.just("c1f2e110-3a4b-4d2c-8b2f-5f6a7c8d9e10"));
        when(estadoRepository.obtenerIdEstadoPorNombre("REVISION_MANUAL"))
                .thenReturn(Mono.just("otro-estado"));

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

        verify(tipoPrestamoRepository).tieneValidacionManual(solicitud.getIdTipoPrestamo());
        verify(estadoRepository).obtenerIdEstadoPorNombre("PENDIENTE_DE_REVISION");
        verify(estadoRepository).obtenerIdEstadoPorNombre("REVISION_MANUAL");
        verify(solicitudRepository).guardarSolicitud(any(Solicitud.class));
    }

    @Test
    void crearSolicitud_exitoso_revision_manual() {
        Solicitud solicitud = buildSolicitudValida();

        when(tipoPrestamoRepository.tieneValidacionManual(solicitud.getIdTipoPrestamo()))
                .thenReturn(Mono.just(true));

        when(estadoRepository.obtenerIdEstadoPorNombre("PENDIENTE_DE_REVISION"))
                .thenReturn(Mono.just("c1f2e110-3a4b-4d2c-8b2f-5f6a7c8d9e10"));
        when(estadoRepository.obtenerIdEstadoPorNombre("REVISION_MANUAL"))
                .thenReturn(Mono.just("otro-estado"));

        when(solicitudRepository.guardarSolicitud(any(Solicitud.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(solicitudUseCase.crearSolicitud(solicitud))
                .assertNext(result -> {
                    assert result.getIdSolicitud() != null;
                    assert result.getFechaCreacion().equals(LocalDate.now());
                    assert result.getIdEstado().equals("otro-estado");
                    assert result.getIdTipoPrestamo().equals("7fa85f64-5717-4562-b3fc-2c963f66afa6");
                })
                .verifyComplete();

        verify(tipoPrestamoRepository).tieneValidacionManual(solicitud.getIdTipoPrestamo());
        verify(estadoRepository).obtenerIdEstadoPorNombre("PENDIENTE_DE_REVISION");
        verify(estadoRepository).obtenerIdEstadoPorNombre("REVISION_MANUAL");
        verify(solicitudRepository).guardarSolicitud(any(Solicitud.class));
    }

    @Test
    void crearSolicitud_error_por_campo_vacio() {
        Solicitud solicitud = buildSolicitudValida().toBuilder()
                .documentoIdentidad(null)
                .build();

        StepVerifier.create(solicitudUseCase.crearSolicitud(solicitud))
                .expectErrorMatches(throwable ->
                        throwable instanceof ValidationException &&
                                throwable.getMessage().equals("El documento de identidad no puede estar vacio")
                )
                .verify();

        verifyNoInteractions(tipoPrestamoRepository, estadoRepository, solicitudRepository);
    }

    @Test
    void crearSolicitud_error_por_campo_monto_vacio() {
        Solicitud solicitud = buildSolicitudValida().toBuilder()
                .monto(null)
                .build();

        StepVerifier.create(solicitudUseCase.crearSolicitud(solicitud))
                .expectErrorMatches(throwable ->
                        throwable instanceof ValidationException &&
                                throwable.getMessage().equals("El monto no puede estar vacio")
                )
                .verify();

        verifyNoInteractions(tipoPrestamoRepository, estadoRepository, solicitudRepository);
    }


    @Test
    void crearSolicitud_errorPorCampoVacioPorEspacios() {
        Solicitud solicitud = buildSolicitudValida().toBuilder()
                .documentoIdentidad("   ")
                .build();

        StepVerifier.create(solicitudUseCase.crearSolicitud(solicitud))
                .expectErrorMatches(throwable ->
                        throwable instanceof ValidationException &&
                                throwable.getMessage().equals("El documento de identidad no puede estar vacio")
                )
                .verify();

        verifyNoInteractions(tipoPrestamoRepository, estadoRepository, solicitudRepository);
    }


    @Test
    void crearSolicitud_errorEnGuardarSolicitud() {
        Solicitud solicitud = buildSolicitudValida();

        when(tipoPrestamoRepository.tieneValidacionManual(solicitud.getIdTipoPrestamo()))
                .thenReturn(Mono.just(false));

        when(estadoRepository.obtenerIdEstadoPorNombre("PENDIENTE_DE_REVISION"))
                .thenReturn(Mono.just("c1f2e110-3a4b-4d2c-8b2f-5f6a7c8d9e10"));
        when(estadoRepository.obtenerIdEstadoPorNombre("REVISION_MANUAL"))
                .thenReturn(Mono.just("otro-uuid"));


        when(solicitudRepository.guardarSolicitud(any(Solicitud.class)))
                .thenReturn(Mono.error(new ValidationException("DB error")));

        StepVerifier.create(solicitudUseCase.crearSolicitud(solicitud))
                .expectErrorMatches(throwable ->
                        throwable instanceof ValidationException &&
                                throwable.getMessage().equals("DB error")
                )
                .verify();

        verify(tipoPrestamoRepository).tieneValidacionManual(solicitud.getIdTipoPrestamo());
        verify(estadoRepository).obtenerIdEstadoPorNombre("PENDIENTE_DE_REVISION");
        verify(solicitudRepository).guardarSolicitud(any(Solicitud.class));
    }
    @Test
    void obtenerSolicitudesPendientes_exitoso() {
        Solicitud solicitud1 = buildSolicitudValida().toBuilder()
                .idSolicitud(UUID.randomUUID().toString())
                .email("user1@test.com")
                .build();
        Solicitud solicitud2 = buildSolicitudValida().toBuilder()
                .idSolicitud(UUID.randomUUID().toString())
                .email("user2@test.com")
                .build();

        when(estadoRepository.obtenerIdEstadoPorNombre("PENDIENTE_DE_REVISION"))
                .thenReturn(Mono.just("id-estado-1"));
        when(estadoRepository.obtenerIdEstadoPorNombre("RECHAZADO"))
                .thenReturn(Mono.just("id-estado-2"));
        when(estadoRepository.obtenerIdEstadoPorNombre("REVISION_MANUAL"))
                .thenReturn(Mono.just("id-estado-3"));
        when(estadoRepository.obtenerIdEstadoPorNombre("APROBADO"))
                .thenReturn(Mono.just("id-estado-aprobado"));
        when(solicitudMapper.toSolicitudPendienteDto(any(Solicitud.class), any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(Mono.just(new SolicitudPendienteDto(
                        "id-solicitud",
                        "123456789",
                        "test@email.com",
                        LocalDate.now(),
                        new BigDecimal("1000.00"),
                        new BigDecimal("2000.00"),
                        new BigDecimal("5.0"),
                        "12",
                        "Tipo Préstamo Test",
                        "Estado Test",
                        new BigDecimal("500.00")
                )));

        when(solicitudRepository.obtenerSolicitudesPendientes(
                anyList(), anyInt(), anyInt(), any(), anyString(), anyString()
        )).thenReturn(Flux.just(solicitud1, solicitud2));

        when(usuarioExternalApiPortUseCase.consultarSalarios(anyList()))
                .thenReturn(Mono.just(List.of(BigDecimal.valueOf(2000), BigDecimal.valueOf(3000))));


        when(estadoRepository.obtenerIdEstadoPorNombre("APROBADO"))
                .thenReturn(Mono.just("id-estado-aprobado"));
        when(solicitudRepository.obtenerSolicitudesPorEstadoAprobado("id-estado-aprobado"))
                .thenReturn(Flux.just(
                        Solicitud.builder()
                                .monto(new BigDecimal("1000.00"))
                                .plazo("12")
                                .build(),
                        Solicitud.builder()
                                .monto(new BigDecimal("2000.00"))
                                .plazo("24")
                                .build()
                ));

        StepVerifier.create(solicitudUseCase.obtenerSolicitudesPendientes(0, 10, null, "DESC", "APROBADO"))
                .expectNextCount(2)
                .verifyComplete();

        verify(solicitudRepository).obtenerSolicitudesPendientes(
                List.of("id-estado-1", "id-estado-2", "id-estado-3"),
                0, 10, null, "DESC", "id-estado-aprobado"
        );
    }




    @Test
    void obtenerSolicitudesPendientes_errorEnEstadoRepository() {

        when(estadoRepository.obtenerIdEstadoPorNombre("PENDIENTE_DE_REVISION"))
                .thenReturn(Mono.error(new RuntimeException("Error al obtener estado")));


        lenient().when(estadoRepository.obtenerIdEstadoPorNombre("RECHAZADO"))
                .thenReturn(Mono.just("id-estado-2"));
        lenient().when(estadoRepository.obtenerIdEstadoPorNombre("REVISION_MANUAL"))
                .thenReturn(Mono.just("id-estado-3"));
        lenient().when(estadoRepository.obtenerIdEstadoPorNombre(""))
                .thenReturn(Mono.just(""));
        lenient().when(estadoRepository.obtenerIdEstadoPorNombre("APROBADO"))
                .thenReturn(Mono.just("id-estado-aprobado"));


        lenient().when(solicitudRepository.obtenerSolicitudesPorEstadoAprobado(anyString()))
                .thenReturn(Flux.empty());
        lenient().when(solicitudRepository.obtenerSolicitudesPendientes(anyList(), anyInt(), anyInt(), any(), anyString(), anyString()))
                .thenReturn(Flux.empty());
        lenient().when(usuarioExternalApiPortUseCase.consultarSalarios(anyList()))
                .thenReturn(Mono.just(List.of()));
        lenient().when(solicitudMapper.toSolicitudPendienteDto(any(Solicitud.class), any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(Mono.just(new SolicitudPendienteDto(
                        "id", "doc", "email", LocalDate.now(),
                        BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE,
                        "12", "tipo", "estado", BigDecimal.ONE
                )));

        StepVerifier.create(solicitudUseCase.obtenerSolicitudesPendientes(0, 10, null, "ASC", null))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                "Error al obtener estado".equals(throwable.getMessage())
                )
                .verify();
    }
    @Test
    void obtenerSolicitudesPendientes_conFiltroPorEstado() {
        Solicitud solicitud = buildSolicitudValida().toBuilder()
                .idSolicitud(UUID.randomUUID().toString())
                .email("user@test.com")
                .build();

        String estadoFiltro = "APROBADO";

        when(estadoRepository.obtenerIdEstadoPorNombre("PENDIENTE_DE_REVISION"))
                .thenReturn(Mono.just("id-estado-1"));
        when(estadoRepository.obtenerIdEstadoPorNombre("RECHAZADO"))
                .thenReturn(Mono.just("id-estado-2"));
        when(estadoRepository.obtenerIdEstadoPorNombre("REVISION_MANUAL"))
                .thenReturn(Mono.just("id-estado-3"));
        when(estadoRepository.obtenerIdEstadoPorNombre(estadoFiltro))
                .thenReturn(Mono.just("id-estado-aprobado"));


        when(estadoRepository.obtenerIdEstadoPorNombre("APROBADO"))
                .thenReturn(Mono.just("id-estado-aprobado"));
        when(solicitudRepository.obtenerSolicitudesPorEstadoAprobado("id-estado-aprobado"))
                .thenReturn(Flux.just(
                        Solicitud.builder()
                                .monto(new BigDecimal("1000.00"))
                                .plazo("12")
                                .build()
                ));

        when(solicitudRepository.obtenerSolicitudesPendientes(
                anyList(), anyInt(), anyInt(), any(), anyString(), anyString()
        )).thenReturn(Flux.just(solicitud));

        when(usuarioExternalApiPortUseCase.consultarSalarios(anyList()))
                .thenReturn(Mono.just(List.of(BigDecimal.valueOf(1800))));


        when(solicitudMapper.toSolicitudPendienteDto(any(Solicitud.class), any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(Mono.just(new SolicitudPendienteDto(
                        "id-solicitud",
                        "123456789",
                        "test@email.com",
                        LocalDate.now(),
                        new BigDecimal("1000.00"),
                        new BigDecimal("1800.00"),
                        new BigDecimal("5.0"),
                        "12",
                        "Tipo Préstamo Test",
                        "Estado Test",
                        new BigDecimal("400.00")
                )));

        StepVerifier.create(solicitudUseCase.obtenerSolicitudesPendientes(0, 10, null, "DESC", estadoFiltro))
                .expectNextCount(1)
                .verifyComplete();

        verify(solicitudRepository).obtenerSolicitudesPendientes(
                List.of("id-estado-1", "id-estado-2", "id-estado-3"),
                0, 10, null, "DESC", "id-estado-aprobado"
        );
    }

    @Test
    void obtenerSolicitudesPendientes_sinSortDirectionUsaAscPorDefecto() {
        Solicitud solicitud = buildSolicitudValida().toBuilder()
                .idSolicitud(UUID.randomUUID().toString())
                .email("user@test.com")
                .build();


        lenient().when(estadoRepository.obtenerIdEstadoPorNombre("PENDIENTE_DE_REVISION"))
                .thenReturn(Mono.just("id-estado-1"));
        lenient().when(estadoRepository.obtenerIdEstadoPorNombre("RECHAZADO"))
                .thenReturn(Mono.just("id-estado-2"));
        lenient().when(estadoRepository.obtenerIdEstadoPorNombre("REVISION_MANUAL"))
                .thenReturn(Mono.just("id-estado-3"));
        lenient().when(estadoRepository.obtenerIdEstadoPorNombre(""))
                .thenReturn(Mono.just(""));


        lenient().when(estadoRepository.obtenerIdEstadoPorNombre("APROBADO"))
                .thenReturn(Mono.just("id-estado-aprobado"));


        lenient().when(solicitudRepository.obtenerSolicitudesPorEstadoAprobado("id-estado-aprobado"))
                .thenReturn(Flux.just(
                        Solicitud.builder()
                                .monto(new BigDecimal("1000.00"))
                                .plazo("12")
                                .build()
                ));

        when(solicitudRepository.obtenerSolicitudesPendientes(
                anyList(), anyInt(), anyInt(), any(), anyString(), anyString()
        )).thenReturn(Flux.just(solicitud));

        when(usuarioExternalApiPortUseCase.consultarSalarios(anyList()))
                .thenReturn(Mono.just(List.of(BigDecimal.valueOf(2500))));

        when(solicitudMapper.toSolicitudPendienteDto(any(Solicitud.class), any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(Mono.just(new SolicitudPendienteDto(
                        "id-solicitud",
                        "123456789",
                        "test@email.com",
                        LocalDate.now(),
                        new BigDecimal("1000.00"),
                        new BigDecimal("2500.00"),
                        new BigDecimal("5.0"),
                        "12",
                        "Tipo Préstamo Test",
                        "Estado Test",
                        new BigDecimal("300.00")
                )));

        StepVerifier.create(solicitudUseCase.obtenerSolicitudesPendientes(1, 5, BigDecimal.valueOf(1000), null, null))
                .expectNextCount(1)
                .verifyComplete();

        verify(solicitudRepository).obtenerSolicitudesPendientes(
                anyList(), eq(1), eq(5), eq(BigDecimal.valueOf(1000)), eq("ASC"), eq("")
        );
    }
    @Test
    void obtenerDeudaMensualAprobada_exitoso() {
        when(estadoRepository.obtenerIdEstadoPorNombre("APROBADO"))
                .thenReturn(Mono.just("id-estado-aprobado"));

        when(solicitudRepository.obtenerSolicitudesPorEstadoAprobado("id-estado-aprobado"))
                .thenReturn(Flux.just(
                        Solicitud.builder()
                                .monto(new BigDecimal("1000.00"))
                                .plazo("12")
                                .build(),
                        Solicitud.builder()
                                .monto(new BigDecimal("2000.00"))
                                .plazo("24")
                                .build()
                ));

        StepVerifier.create(solicitudUseCase.obtenerDeudaMensualAprobada())
                .expectNext(new BigDecimal("166.66"))
                .verifyComplete();

        verify(estadoRepository).obtenerIdEstadoPorNombre("APROBADO");
        verify(solicitudRepository).obtenerSolicitudesPorEstadoAprobado("id-estado-aprobado");
    }

    @Test
    void obtenerDeudaMensualAprobada_sinSolicitudesRetornaCero() {
        when(estadoRepository.obtenerIdEstadoPorNombre("APROBADO"))
                .thenReturn(Mono.just("id-estado-aprobado"));
        when(solicitudRepository.obtenerSolicitudesPorEstadoAprobado("id-estado-aprobado"))
                .thenReturn(Flux.empty());

        StepVerifier.create(solicitudUseCase.obtenerDeudaMensualAprobada())
                .expectNext(BigDecimal.ZERO)
                .verifyComplete();
    }


    @Test
    void obtenerDeudaMensualAprobada_plazoCeroLanzaExcepcion() {
        when(estadoRepository.obtenerIdEstadoPorNombre("APROBADO"))
                .thenReturn(Mono.just("id-estado-aprobado"));
        when(solicitudRepository.obtenerSolicitudesPorEstadoAprobado("id-estado-aprobado"))
                .thenReturn(Flux.just(
                        Solicitud.builder()
                                .monto(new BigDecimal("1000.00"))
                                .plazo("0")
                                .build()
                ));

        StepVerifier.create(solicitudUseCase.obtenerDeudaMensualAprobada())
                .expectError(ArithmeticException.class)
                .verify();
    }

    @Test
    void actualizarEstadoSolicitud_exito() {
        String idSolicitud = "123";
        String nuevoEstado = "APROBADA";

        Solicitud solicitud = Solicitud.builder()
                .idSolicitud(idSolicitud)
                .documentoIdentidad("11111111")
                .email("test@email.com")
                .monto(new BigDecimal("5000.00"))
                .plazo("12")
                .idTipoPrestamo("TP1")
                .fechaCreacion(LocalDate.of(2025, 9, 10))
                .idEstado("ESTADO_ANTERIOR")
                .build();

        String nuevoEstadoId = "ESTADO_NUEVO";

        Solicitud solicitudActualizada = solicitud.toBuilder()
                .idEstado(nuevoEstadoId)
                .build();

        when(solicitudRepository.obtenerSolicitudPorId(idSolicitud)).thenReturn(Mono.just(solicitud));
        when(estadoRepository.obtenerIdEstadoPorNombre(nuevoEstado)).thenReturn(Mono.just(nuevoEstadoId));
        when(solicitudRepository.actualizarSolicitud(any(Solicitud.class))).thenReturn(Mono.just(solicitudActualizada));

        StepVerifier.create(solicitudUseCase.actualizarEstadoSolicitud(idSolicitud, nuevoEstado))
                .expectNextMatches(result ->
                        result.getIdSolicitud().equals(idSolicitud) &&
                                result.getIdEstado().equals(nuevoEstadoId)
                )
                .verifyComplete();

        verify(solicitudRepository).obtenerSolicitudPorId(idSolicitud);
        verify(estadoRepository).obtenerIdEstadoPorNombre(nuevoEstado);
        verify(solicitudRepository).actualizarSolicitud(any(Solicitud.class));
    }

    @Test
    void actualizarEstadoSolicitud_error() {
        String idSolicitud = "123";
        String nuevoEstado = "APROBADA";

        when(solicitudRepository.obtenerSolicitudPorId(idSolicitud))
                .thenReturn(Mono.error(new RuntimeException("DB error")));
        when(estadoRepository.obtenerIdEstadoPorNombre(nuevoEstado))
                .thenReturn(Mono.never());

        StepVerifier.create(solicitudUseCase.actualizarEstadoSolicitud(idSolicitud, nuevoEstado))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("DB error"))
                .verify();

        verify(solicitudRepository).obtenerSolicitudPorId(idSolicitud);
        verify(estadoRepository).obtenerIdEstadoPorNombre(nuevoEstado);
        verify(solicitudRepository, never()).actualizarSolicitud(any(Solicitud.class));
    }

    @Test
    void obtenerSolicitudesPorEstadoAprobado_retornaFluxDeSolicitudes() {
        String email = "user@test.com";
        String estadoId = "123";
        Solicitud solicitud1 = Solicitud.builder().idSolicitud("sol1").build();
        Solicitud solicitud2 = Solicitud.builder().idSolicitud("sol2").build();

        when(estadoRepository.obtenerIdEstadoPorNombre("APROBADO"))
                .thenReturn(Mono.just(estadoId));

        when(solicitudRepository.obtenerSolicitudesAprobadasPorUsuario(email, estadoId))
                .thenReturn(Flux.just(solicitud1, solicitud2));

        Flux<Solicitud> result = solicitudUseCase.obtenerSolicitudesPorEstadoAprobado(email);

        StepVerifier.create(result)
                .expectNext(solicitud1)
                .expectNext(solicitud2)
                .verifyComplete();

        verify(estadoRepository).obtenerIdEstadoPorNombre("APROBADO");
        verify(solicitudRepository).obtenerSolicitudesAprobadasPorUsuario(email, estadoId);
    }



    @Test
    void obtenerSolicitudesPorEstadoAprobado_retornaVacioSiNoHaySolicitudes() {
        String email = "user@test.com";
        String estadoId = "123";

        when(estadoRepository.obtenerIdEstadoPorNombre("APROBADO"))
                .thenReturn(Mono.just(estadoId));

        when(solicitudRepository.obtenerSolicitudesAprobadasPorUsuario(email, estadoId))
                .thenReturn(Flux.empty());

        Flux<Solicitud> result = solicitudUseCase.obtenerSolicitudesPorEstadoAprobado(email);

        StepVerifier.create(result)
                .verifyComplete();

        verify(estadoRepository).obtenerIdEstadoPorNombre("APROBADO");
        verify(solicitudRepository).obtenerSolicitudesAprobadasPorUsuario(email, estadoId);
    }

    @Test
    void tieneValidacionAutomatica_retornaTrue() {
        String idTipoPrestamo = "tipo1";
        when(tipoPrestamoRepository.tieneValidacionAutomatica(idTipoPrestamo))
                .thenReturn(Mono.just(true));

        Mono<Boolean> result = solicitudUseCase.tieneValidacionAutomatica(idTipoPrestamo);

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        verify(tipoPrestamoRepository).tieneValidacionAutomatica(idTipoPrestamo);
    }

    @Test
    void tieneValidacionAutomatica_retornaFalse() {
        String idTipoPrestamo = "tipo2";
        when(tipoPrestamoRepository.tieneValidacionAutomatica(idTipoPrestamo))
                .thenReturn(Mono.just(false));

        Mono<Boolean> result = solicitudUseCase.tieneValidacionAutomatica(idTipoPrestamo);

        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();

        verify(tipoPrestamoRepository).tieneValidacionAutomatica(idTipoPrestamo);
    }

}