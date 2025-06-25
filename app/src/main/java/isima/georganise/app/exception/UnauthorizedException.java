package isima.georganise.app.exception;

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException() {
        super("Unauthorized");
    }

    public UnauthorizedException(String userName, String action) {
        super("User " + userName + " is not authorized to " + action);
    }
}
