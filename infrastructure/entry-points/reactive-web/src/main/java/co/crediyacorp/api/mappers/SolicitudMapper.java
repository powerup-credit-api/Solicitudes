package co.crediyacorp.api.mappers;

import co.crediyacorp.api.dtos.SolicitudEntradaDto;
import co.crediyacorp.model.solicitud.Solicitud;
import co.crediyacorp.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.crediyacorp.usecase.crearsolicitud.excepciones.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class SolicitudMapper {
    private final TipoPrestamoRepository tipoPrestamoRepository;

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




    }

