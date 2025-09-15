package co.crediyacorp.externo;

import co.crediyacorp.model.external_services.ExternalPusbliser;
import co.crediyacorp.model.external_services.utils.DomainEventPublisher;
import co.crediyacorp.model.solicitud.SolicitudAprobadaEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;


@RequiredArgsConstructor
@Component
public class ExternaPublisherImpl implements ExternalPusbliser, DomainEventPublisher {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final SqsProperties sqsProperties;



    private Mono<Void> enviar(Object evento, String queueUrl) {
        return Mono.fromCallable(() -> {
                    String json = objectMapper.writeValueAsString(evento);
                    SendMessageRequest request = SendMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .messageBody(json)
                            .build();

                    sqsClient.sendMessage(request);
                    return null;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }


    @Override
    public Mono<Void> enviarEmail(Object evento) {
        return enviar(evento, sqsProperties.email());
    }

    @Override
    public Mono<Void> enviarValidacionAutomatica(Object evento) {
        return enviar(evento, sqsProperties.validacion());
    }

    @Override
    public Mono<Void> publishSolicitudAprobada(SolicitudAprobadaEvent event) {
        return enviar(event, sqsProperties.solicitudAprobada());

    }
}
