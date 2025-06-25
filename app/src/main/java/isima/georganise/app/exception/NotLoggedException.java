package isima.georganise.app.exception;

public class NotLoggedException extends RuntimeException {

    public NotLoggedException() {
        super("User is not logged in");
    }

    public NotLoggedException(String message) {
        super(message + " User is not logged in");
    }
}
