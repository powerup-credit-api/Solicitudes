package co.crediyacorp.api.mappers;

import co.crediyacorp.api.dtos.RespuestaDto;

import co.crediyacorp.api.dtos.SolicitudAprobadaDto;
import co.crediyacorp.api.dtos.SolicitudEntradaDto;
import co.crediyacorp.api.dtos.ValidacionAutomaticaSalidaDto;
import co.crediyacorp.model.estado.gateways.EstadoRepository;
import co.crediyacorp.model.solicitud.Solicitud;
import co.crediyacorp.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.crediyacorp.model.excepciones.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;


@RequiredArgsConstructor
@Component
public class SolicitudMapper {
    private final TipoPrestamoRepository tipoPrestamoRepository;
    private final EstadoRepository estadoRepository;

    public Mono<Solicitud> toDomain(SolicitudEntradaDto entradaDto) {
        return tipoPrestamoRepository.existeTipoPrestamoPorNombre(
                        entradaDto.tipoPrestamo().trim().toUpperCase()
                )
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(new ValidationException("El tipo de préstamo no existe")))
                .then(tipoPrestamoRepository.obtenerIdTipoPrestamoPorNombre(entradaDto.tipoPrestamo()))
                .map(tipoPrestamoId -> Solicitud.builder()
                        .email(entradaDto.email())
                        .monto(entradaDto.monto())
                        .documentoIdentidad(entradaDto.documentoIdentidad())
                        .plazo(entradaDto.plazo())
                        .idTipoPrestamo(tipoPrestamoId)
                        .build()
                );
    }




    public Mono<RespuestaDto> toResponse(Solicitud solicitud) {
        return Mono.zip(
                estadoRepository.obtenerNombreEstadoPorId(solicitud.getIdEstado()),
                tipoPrestamoRepository.obtenerNombreTipoPrestamoPorId(solicitud.getIdTipoPrestamo())
        ).map(tuple -> new RespuestaDto(
                solicitud.getIdSolicitud(),
                solicitud.getDocumentoIdentidad(),
                solicitud.getEmail(),
                solicitud.getFechaCreacion(),
                solicitud.getMonto(),
                solicitud.getPlazo(),
                tuple.getT2(),
                tuple.getT1()
        ));
    }

    public Mono<ValidacionAutomaticaSalidaDto> toValidacionAutomaticaSalidaDto(
            Solicitud solicitud,
            List<Solicitud> solicitudesAprobadas,
            BigDecimal salarioBase) {

        return Mono.zip(
                        tipoPrestamoRepository.obtenerTasaInteresPorIdTipoPrestamo(solicitud.getIdTipoPrestamo()),
                        tipoPrestamoRepository.obtenerNombreTipoPrestamoPorId(solicitud.getIdTipoPrestamo()))
                .flatMap(tuple -> {
                    BigDecimal tasaInteres = tuple.getT1();
                    String nombreTipoPrestamo = tuple.getT2();

                    return Flux.fromIterable(solicitudesAprobadas)
                            .flatMap(this::toSolicitudAprobadaDto)
                            .collectList()
                            .map(aprobadasConTasa -> new ValidacionAutomaticaSalidaDto(
                                    solicitud.getIdSolicitud(),
                                    solicitud.getDocumentoIdentidad(),
                                    solicitud.getEmail(),
                                    solicitud.getMonto(),
                                    solicitud.getPlazo(),
                                    nombreTipoPrestamo,
                                    tasaInteres,
                                    salarioBase,
                                    aprobadasConTasa
                            ));
                });
    }



    private Mono<SolicitudAprobadaDto> toSolicitudAprobadaDto(Solicitud solicitud) {
        return tipoPrestamoRepository
                .obtenerTasaInteresPorIdTipoPrestamo(solicitud.getIdTipoPrestamo())
                .map(tasa -> new SolicitudAprobadaDto(
                        solicitud.getIdSolicitud(),
                        solicitud.getMonto(),
                        solicitud.getPlazo(),
                        tasa
                ));
    }




}

