package casinoescape.logging;

public class LogEntry {
    private final int turn;
    private final String message;

    public LogEntry(String message) {
        this(-1, message);
    }

    public LogEntry(int turn, String message) {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Log message is required");
        }
        this.turn = turn;
        this.message = message;
    }

    public int getTurn() {
        return turn;
    }

    public String getMessage() {
        return message;
    }

    public boolean hasTurn() {
        return turn >= 0;
    }
}
