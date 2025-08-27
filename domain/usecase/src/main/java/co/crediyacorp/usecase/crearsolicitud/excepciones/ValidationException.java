package co.crediyacorp.usecase.crearsolicitud.excepciones;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
