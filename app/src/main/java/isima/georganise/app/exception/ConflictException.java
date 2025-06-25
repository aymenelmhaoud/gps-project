package isima.georganise.app.exception;

public class ConflictException extends RuntimeException {

    public ConflictException() {
        super("Conflit");
    }

    public ConflictException(String message) {
        super(message);
    }
}
