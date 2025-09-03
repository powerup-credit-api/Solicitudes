package co.crediyacorp.usecase.crearsolicitud.usecases;

import co.crediyacorp.model.estado.gateways.EstadoRepository;
import co.crediyacorp.model.solicitud.Solicitud;
import co.crediyacorp.model.solicitud.gateways.SolicitudRepository;
import co.crediyacorp.model.excepciones.ValidationException;
import co.crediyacorp.model.tipoprestamo.gateways.TipoPrestamoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log
@RequiredArgsConstructor
public class SolicitudUseCase {

    private final SolicitudRepository solicitudRepository;
    private final EstadoRepository estadoRepository;
    private final TipoPrestamoRepository tipoPrestamoRepository;

    public Mono<Solicitud> crearSolicitud(Solicitud solicitud) {
        return validarCamposVacios(solicitud)
                .then(Mono.defer(() -> tipoPrestamoRepository.tieneValidacionManual(solicitud.getIdTipoPrestamo())
                        .flatMap(tieneValidacion -> Mono.zip(
                                    estadoRepository.obtenerIdEstadoPorNombre("PENDIENTE_DE_REVISION")
                                    ,estadoRepository.obtenerIdEstadoPorNombre("REVISION_MANUAL"))
                                    .map(tuple ->
                                        Boolean.TRUE.equals(tieneValidacion)
                                                ? tuple.getT2()
                                                : tuple.getT1())
                                    .map(estado -> {
                                        solicitud.setIdEstado(estado);
                                        return solicitud;
                                    }))))
                .doOnNext(solicitudIncompleta -> {
                    solicitudIncompleta.setIdSolicitud(UUID.randomUUID().toString());
                    solicitudIncompleta.setFechaCreacion(LocalDate.now());
                })
                .flatMap(solicitudRepository::guardarSolicitud)
                .doOnSuccess(solicitudExitosa -> log.info("Solicitud guardada correctamente con ID " + solicitudExitosa.getIdSolicitud()))
                .doOnError(e -> log.severe("Error al crear la solicitud: " + e.getMessage()));
    }

    public Flux<Solicitud> obtenerSolicitudesPendientes(Integer page, Integer size, BigDecimal monto, String sortDirection,String nombreEstado) {
        return Mono.zip(
                        estadoRepository.obtenerIdEstadoPorNombre("PENDIENTE_DE_REVISION"),
                        estadoRepository.obtenerIdEstadoPorNombre("RECHAZADO"),
                        estadoRepository.obtenerIdEstadoPorNombre("REVISION_MANUAL"),
                        nombreEstado != null ? estadoRepository.obtenerIdEstadoPorNombre(nombreEstado)
                                : Mono.just("")
                )
                .flatMapMany(tuple ->
                        solicitudRepository.obtenerSolicitudesPendientes(
                                List.of(tuple.getT1(), tuple.getT2(), tuple.getT3()),
                                page,
                                size,
                                monto,
                                Optional.ofNullable(sortDirection).orElse("ASC")
                                ,tuple.getT4()

                        )
                )
                .doOnError(e -> log.severe("Error al obtener solicitudes pendientes: " + e.getMessage()))
                .doOnComplete(() -> log.info("Obtencion de solicitudes pendientes completada"));
    }

    public Mono<BigDecimal> obtenerDeudaMensualAprobada(){
        return estadoRepository.obtenerIdEstadoPorNombre("APROBADO")
                .flatMap(estadoAprobadoId -> solicitudRepository.obtenerSolicitudesPorEstadoAprobado(estadoAprobadoId)
                        .map(solicitud ->
                            solicitud.getMonto().divide(new BigDecimal(solicitud.getPlazo()), RoundingMode.HALF_UP))
                        .reduce(BigDecimal::add)
                        .defaultIfEmpty(BigDecimal.ZERO)
                )
                .doOnError(e -> log.severe("Error al obtener la deuda mensual aprobada: " + e.getMessage()))
                .doOnSuccess(deudaMensual -> log.info("Deuda mensual aprobada calculada correctamente: " + deudaMensual));
    }



    public Mono<Void> validarCamposVacios(Solicitud solicitud) {
        return Flux.concat(

                        validarCampo(solicitud.getDocumentoIdentidad(), "El documento de identidad no puede estar vacio"),
                        validarCampo(solicitud.getEmail(), "El email no puede estar vacio"),
                        validarCampo(
                                solicitud.getMonto() == null ? null : solicitud.getMonto().toString(),
                                "El monto no puede estar vacio"
                        ),
                        validarCampo(solicitud.getPlazo(), "El plazo no puede estar vacio"),
                        validarCampo(solicitud.getIdTipoPrestamo(), "El tipo de prestamo no puede estar vacio")

                )
                .next();
    }

    public Mono<Void> validarCampo(String valor, String mensajeError) {
        return (valor == null || valor.trim().isEmpty())
                ? Mono.error(new ValidationException(mensajeError))
                : Mono.empty();
    }

}
