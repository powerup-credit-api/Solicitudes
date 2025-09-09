package co.crediyacorp.model.solicitud.gateways;

import co.crediyacorp.model.solicitud.Solicitud;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import java.util.List;

public interface SolicitudRepository {

    Mono<Solicitud> guardarSolicitud(Solicitud solicitud);
    Flux<Solicitud> obtenerSolicitudesPorEstadoAprobado(String idEstado);

    Mono<Solicitud> obtenerSolicitudPorId(String idSolicitud);

    Mono<Solicitud> actualizarSolicitud(Solicitud solicitud);

    Flux<Solicitud> obtenerSolicitudesPendientes(List<String> estados, Integer page, Integer size, BigDecimal monto, String sortDirection, String estadoId);
}
