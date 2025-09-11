package co.crediyacorp.externo;

import co.crediyacorp.model.external_services.ExternalPusbliser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;


@RequiredArgsConstructor
@Component
public class ExternaPublisherImpl implements ExternalPusbliser {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final SqsProperties sqsProperties;

    @Override
    public Mono<Void> enviar(Object evento) {

        return Mono.fromCallable(() -> {
                    String json = objectMapper.writeValueAsString(evento);
                    SendMessageRequest request = SendMessageRequest.builder()
                            .queueUrl(sqsProperties.loanQueueUrl())
                            .messageBody(json)
                            .build();

                    sqsClient.sendMessage(request);

                    return null;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }


}
