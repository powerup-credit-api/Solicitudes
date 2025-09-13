package co.crediyacorp.externo;

import co.crediyacorp.usecase.crearsolicitud.transaction_usecase.ExecuteSolicitudUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Log
public class SqsListener {


    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final SqsProperties sqsProperties;
    private final ExecuteSolicitudUseCase executeSolicitudUseCase;

    public void startListening() {
        pollQueue()
                .repeat()
                .subscribe();
    }

    private Mono<Void> pollQueue() {
        return Mono.fromCallable(() -> {
                    ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                            .queueUrl(sqsProperties.respuesta())
                            .maxNumberOfMessages(10)
                            .waitTimeSeconds(20)
                            .build();

                    return sqsClient.receiveMessage(request);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(response -> Flux.fromIterable(response.messages()))
                .flatMap(this::processMessage)
                .then();
    }

    private Mono<Void> processMessage(Message message) {
        return Mono.fromCallable(() -> objectMapper.readValue(message.body(), DecisionDto.class))
                .flatMap(decision ->
                    executeSolicitudUseCase.executeActualizarSolicitud(decision.idSolicitud(), decision.nuevoEstado())
                )
                .then(Mono.fromRunnable(() -> {

                    DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                            .queueUrl(sqsProperties.respuesta())
                            .receiptHandle(message.receiptHandle())
                            .build();
                    sqsClient.deleteMessage(deleteRequest);
                    log.info("mensaje de validacion automatica recibido correctamente desde solicitudes");
                }))
                .onErrorResume(e -> {
                    log.severe("error recibiendo mensaje externo" + e.getMessage());
                    return Mono.empty();
                })

                .subscribeOn(Schedulers.boundedElastic()).then();
    }
}
