package casinoescape.exceptions;

public class LockedDoorException extends IllegalStateException {
    public LockedDoorException(String message) {
        super(message);
    }

    public LockedDoorException(String message, Throwable cause) {
        super(message, cause);
    }
}
