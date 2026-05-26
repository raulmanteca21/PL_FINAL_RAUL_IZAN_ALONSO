package casinoescape.exceptions;

public class InvalidActionException extends IllegalStateException {
    public InvalidActionException(String message) {
        super(message);
    }

    public InvalidActionException(String message, Throwable cause) {
        super(message, cause);
    }
}
