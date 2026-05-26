package casinoescape.model;

public class Trap {
    public static final String DANGEROUS_COMPANION_ID = "DANGEROUS_COMPANION";

    private final String id;
    private final String name;
    private final int roomId;
    private final Position position;
    private final int damagePercent;

    public Trap(String id, String name, int roomId, Position position, int damagePercent) {
        requireText(id, "Trap id is required");
        requireText(name, "Trap name is required");
        if (roomId <= 0) {
            throw new IllegalArgumentException("Trap room id must be positive");
        }
        if (position == null) {
            throw new IllegalArgumentException("Trap position is required");
        }
        if (damagePercent < 0 || damagePercent > 100) {
            throw new IllegalArgumentException("Trap damage percent must be between 0 and 100");
        }
        this.id = id;
        this.name = name;
        this.roomId = roomId;
        this.position = position;
        this.damagePercent = damagePercent;
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

    public int getDamagePercent() {
        return damagePercent;
    }

    private void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}
