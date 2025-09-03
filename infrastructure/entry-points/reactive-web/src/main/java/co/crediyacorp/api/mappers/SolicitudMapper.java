package co.crediyacorp.api.mappers;

import co.crediyacorp.api.dtos.RespuestaDto;
import co.crediyacorp.api.dtos.SolicitudPendienteDto;
import co.crediyacorp.api.dtos.SolicitudEntradaDto;
import co.crediyacorp.model.estado.gateways.EstadoRepository;
import co.crediyacorp.model.solicitud.Solicitud;
import co.crediyacorp.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.crediyacorp.model.excepciones.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

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


    public Mono<SolicitudPendienteDto> toSolicitudPendienteDto (Solicitud solicitud, BigDecimal salarioBase, BigDecimal deudaMensualAprobadas) {
        return Mono.zip(
                estadoRepository.obtenerNombreEstadoPorId(solicitud.getIdEstado()),
                tipoPrestamoRepository.obtenerNombreTipoPrestamoPorId(solicitud.getIdTipoPrestamo()),
                tipoPrestamoRepository.obtenerTasaInteresPorIdTipoPrestamo(solicitud.getIdTipoPrestamo())
        ).map(tuple -> new SolicitudPendienteDto(
                solicitud.getIdSolicitud(),
                solicitud.getDocumentoIdentidad(),
                solicitud.getEmail(),
                solicitud.getFechaCreacion(),
                solicitud.getMonto(),
                salarioBase,
                tuple.getT3(),
                solicitud.getPlazo(),
                tuple.getT2(),
                tuple.getT1(),
                deudaMensualAprobadas
        ));
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



    }

