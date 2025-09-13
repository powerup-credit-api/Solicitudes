package co.crediyacorp.externo;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SqsListenerRunner implements ApplicationRunner {

    private final SqsListener sqsListener;

    @Override
    public void run(ApplicationArguments args) {
        sqsListener.startListening();
    }
}
