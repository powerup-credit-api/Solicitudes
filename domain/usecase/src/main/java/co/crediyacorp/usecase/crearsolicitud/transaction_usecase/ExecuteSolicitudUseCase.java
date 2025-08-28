package co.crediyacorp.usecase.crearsolicitud.transaction_usecase;

import co.crediyacorp.model.solicitud.Solicitud;
import co.crediyacorp.model.transactions.TransactionalWrapper;
import co.crediyacorp.usecase.crearsolicitud.usecases.SolicitudUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ExecuteSolicitudUseCase {
    private final TransactionalWrapper transactionalWrapper;
    private final SolicitudUseCase solicitudUseCase;

    public Mono<Solicitud> executeGuardarSolicitud(Solicitud solicitud) {
        return transactionalWrapper.executeInTransaction(solicitudUseCase.crearSolicitud(solicitud));
    }

}
