package co.crediyacorp.model.excepciones;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
