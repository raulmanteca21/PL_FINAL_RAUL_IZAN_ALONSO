package casinoescape.movement;

import casinoescape.model.Position;
import casinoescape.structures.MyLinkedList;

public class ShortestPathInfo {
    public static final int NO_RECOMMENDED_ROOM = -1;
    public static final int NO_DISTANCE = -1;

    private final int currentRoomId;
    private final int targetRoomId;
    private final MyLinkedList<Integer> roomPath;
    private final int roomDistance;
    private final int recommendedNextRoomId;
    private final Position recommendedTargetPosition;
    private final MyLinkedList<Position> cellPath;
    private final int cellDistance;
    private final boolean alreadyInExitRoom;

    public ShortestPathInfo(int currentRoomId, int targetRoomId, MyLinkedList<Integer> roomPath,
            int roomDistance, int recommendedNextRoomId, Position recommendedTargetPosition,
            MyLinkedList<Position> cellPath, int cellDistance, boolean alreadyInExitRoom) {
        if (currentRoomId <= 0 || targetRoomId <= 0) {
            throw new IllegalArgumentException("Room ids must be positive");
        }
        if (roomPath == null) {
            throw new IllegalArgumentException("Room path is required");
        }
        if (cellPath == null) {
            throw new IllegalArgumentException("Cell path is required");
        }
        this.currentRoomId = currentRoomId;
        this.targetRoomId = targetRoomId;
        this.roomPath = roomPath;
        this.roomDistance = roomDistance;
        this.recommendedNextRoomId = recommendedNextRoomId;
        this.recommendedTargetPosition = recommendedTargetPosition;
        this.cellPath = cellPath;
        this.cellDistance = cellDistance;
        this.alreadyInExitRoom = alreadyInExitRoom;
    }

    public int getCurrentRoomId() {
        return currentRoomId;
    }

    public int getTargetRoomId() {
        return targetRoomId;
    }

    public MyLinkedList<Integer> getRoomPath() {
        return roomPath;
    }

    public int getRoomDistance() {
        return roomDistance;
    }

    public int getRecommendedNextRoomId() {
        return recommendedNextRoomId;
    }

    public Position getRecommendedTargetPosition() {
        return recommendedTargetPosition;
    }

    public MyLinkedList<Position> getCellPath() {
        return cellPath;
    }

    public int getCellDistance() {
        return cellDistance;
    }

    public boolean isAlreadyInExitRoom() {
        return alreadyInExitRoom;
    }

    public boolean hasRoomPath() {
        return !roomPath.isEmpty();
    }

    public boolean hasCellPath() {
        return !cellPath.isEmpty();
    }
}
