package hexlet.code.app.exception;

public class ForbiddenOperationException extends RuntimeException {

    public ForbiddenOperationException(String message) {
        super(message);
    }
}
