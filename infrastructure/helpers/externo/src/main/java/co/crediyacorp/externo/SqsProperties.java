package co.crediyacorp.externo;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "aws.sqs")
public record SqsProperties(
        String email,
        String validacion,
        String respuesta,
        String solicitudAprobada
) {
}
