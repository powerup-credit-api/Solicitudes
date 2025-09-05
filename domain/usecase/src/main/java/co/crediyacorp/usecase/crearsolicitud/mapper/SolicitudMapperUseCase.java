package co.crediyacorp.usecase.crearsolicitud.mapper;

import co.crediyacorp.model.estado.gateways.EstadoRepository;
import co.crediyacorp.model.solicitud.Solicitud;
import co.crediyacorp.model.solicitud.SolicitudPendienteDto;
import co.crediyacorp.model.tipoprestamo.gateways.TipoPrestamoRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class SolicitudMapperUseCase
{

    private final EstadoRepository estadoRepository;
    private final TipoPrestamoRepository tipoPrestamoRepository;
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

}
