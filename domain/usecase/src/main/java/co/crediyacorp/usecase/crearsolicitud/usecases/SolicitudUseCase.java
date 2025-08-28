package co.crediyacorp.usecase.crearsolicitud.usecases;

import co.crediyacorp.model.estado.gateways.EstadoRepository;
import co.crediyacorp.model.solicitud.Solicitud;
import co.crediyacorp.model.solicitud.gateways.SolicitudRepository;
import co.crediyacorp.usecase.crearsolicitud.excepciones.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

@Log
@RequiredArgsConstructor
public class SolicitudUseCase {

    private final SolicitudRepository solicitudRepository;
    private final EstadoRepository estadoRepository;

    public Mono<Solicitud> crearSolicitud(Solicitud solicitud) {
        return validarCamposVacios(solicitud)
                .then(estadoRepository.obtenerIdEstadoPorNombre("PENDIENTE_DE_REVISION")
                        .flatMap(idEstado -> {
                            solicitud.setIdEstado(idEstado);
                            return Mono.just(solicitud);
                        })
                )
                .doOnNext(solicitudIncompleta -> {
                    solicitudIncompleta.setIdSolicitud(UUID.randomUUID().toString());
                    solicitudIncompleta.setFechaCreacion(LocalDate.now());
                })
                .flatMap(solicitudRepository::guardarSolicitud)
                .doOnSuccess(solicitudExitosa -> log.info("Solicitud guardada correctamente con ID " + solicitudExitosa.getIdSolicitud()))
                .doOnError(e -> log.severe("Error al crear la solicitud: " + e.getMessage()));
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
