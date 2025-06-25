package isima.georganise.app.exception;

public class WrongPasswordException extends RuntimeException {

    public WrongPasswordException() {
        super("Wrong password.");
    }

    public WrongPasswordException(String message) {
        super("Wrong password for: " + message);
    }
}
