package casinoescape.model;

public class Door {
    private final int destinationRoomId;
    private final boolean locked;
    private final String requiredKeyName;

    public Door(int destinationRoomId) {
        this(destinationRoomId, false, "");
    }

    public Door(int destinationRoomId, boolean locked, String requiredKeyName) {
        if (destinationRoomId <= 0) {
            throw new IllegalArgumentException("Destination room id must be positive");
        }
        if (locked && (requiredKeyName == null || requiredKeyName.isBlank())) {
            throw new IllegalArgumentException("Locked doors require a key name");
        }
        this.destinationRoomId = destinationRoomId;
        this.locked = locked;
        this.requiredKeyName = requiredKeyName == null ? "" : requiredKeyName;
    }

    public int getDestinationRoomId() {
        return destinationRoomId;
    }

    public boolean isLocked() {
        return locked;
    }

    public String getRequiredKeyName() {
        return requiredKeyName;
    }

    public boolean canPass(boolean hasRequiredKey) {
        return !locked || hasRequiredKey;
    }
}
