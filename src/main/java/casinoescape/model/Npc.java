package casinoescape.model;

public class Npc {
    public static final String WELCOME_NPC_ID = "WELCOME_NPC";
    public static final String BAR_SPECIAL_NPC_ID = "BAR_SPECIAL_NPC";
    public static final String FRIEND_NPC_ID = "FRIEND_NPC";

    private final String id;
    private final String name;
    private final int roomId;
    private final Position position;
    private final String message;
    private boolean alreadyInteracted;

    public Npc(String id, String name, int roomId, Position position, String message) {
        requireText(id, "Npc id is required");
        requireText(name, "Npc name is required");
        if (roomId <= 0) {
            throw new IllegalArgumentException("Npc room id must be positive");
        }
        if (position == null) {
            throw new IllegalArgumentException("Npc position is required");
        }
        this.id = id;
        this.name = name;
        this.roomId = roomId;
        this.position = position;
        this.message = message == null ? "" : message;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getRoomId() {
        return roomId;
    }

    public Position getPosition() {
        return position;
    }

    public String getMessage() {
        return message;
    }

    public boolean hasAlreadyInteracted() {
        return alreadyInteracted;
    }

    public void markInteracted() {
        alreadyInteracted = true;
    }

    private void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}
