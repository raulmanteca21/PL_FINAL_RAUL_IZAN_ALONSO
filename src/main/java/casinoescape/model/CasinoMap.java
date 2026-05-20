package casinoescape.model;

import casinoescape.structures.MyGraph;
import casinoescape.structures.MyLinkedList;

public class CasinoMap {
    public static final int EXIT_ROOM_ID = 8;

    private final MyGraph<Integer> roomGraph;
    private final MyLinkedList<Room> rooms;
    private final int initialRoomId;
    private final Position initialPlayerPosition;

    public CasinoMap(MyGraph<Integer> roomGraph, MyLinkedList<Room> rooms, int initialRoomId, Position initialPlayerPosition) {
        if (roomGraph == null) {
            throw new IllegalArgumentException("Room graph is required");
        }
        if (rooms == null || rooms.isEmpty()) {
            throw new IllegalArgumentException("Rooms are required");
        }
        if (initialRoomId <= 0) {
            throw new IllegalArgumentException("Initial room id must be positive");
        }
        if (initialPlayerPosition == null) {
            throw new IllegalArgumentException("Initial player position is required");
        }
        this.roomGraph = roomGraph;
        this.rooms = rooms;
        this.initialRoomId = initialRoomId;
        this.initialPlayerPosition = initialPlayerPosition;
    }

    public int getRoomCount() {
        return rooms.size();
    }

    public Room getRoom(int roomId) {
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            if (room.getId() == roomId) {
                return room;
            }
        }
        throw new IllegalArgumentException("Room does not exist: " + roomId);
    }

    public boolean areRoomsConnected(int fromRoomId, int toRoomId) {
        return roomGraph.areConnected(fromRoomId, toRoomId);
    }

    public MyLinkedList<Integer> getConnectedRooms(int roomId) {
        return roomGraph.getNeighbors(roomId);
    }

    public boolean hasDoorTo(int fromRoomId, int toRoomId) {
        return findDoorTo(fromRoomId, toRoomId) != null;
    }

    public Door getDoorTo(int fromRoomId, int toRoomId) {
        Door door = findDoorTo(fromRoomId, toRoomId);
        if (door == null) {
            throw new IllegalArgumentException("Door does not exist from room " + fromRoomId + " to room " + toRoomId);
        }
        return door;
    }

    public boolean canTransition(int fromRoomId, int toRoomId, boolean hasTreasuryKey) {
        if (!areRoomsConnected(fromRoomId, toRoomId)) {
            return false;
        }
        Door door = findDoorTo(fromRoomId, toRoomId);
        return door != null && door.canPass(hasTreasuryKey);
    }

    public boolean roomHasCellType(int roomId, CellType type) {
        return countCellsOfType(roomId, type) > 0;
    }

    public int countCellsOfType(int roomId, CellType type) {
        if (type == null) {
            throw new IllegalArgumentException("Cell type is required");
        }
        Room room = getRoom(roomId);
        int count = 0;
        for (int row = 0; row < room.getRows(); row++) {
            for (int column = 0; column < room.getColumns(); column++) {
                if (room.getCell(new Position(row, column)).getType() == type) {
                    count++;
                }
            }
        }
        return count;
    }

    public int getInitialRoomId() {
        return initialRoomId;
    }

    public Position getInitialPlayerPosition() {
        return initialPlayerPosition;
    }

    private Door findDoorTo(int fromRoomId, int toRoomId) {
        Room room = getRoom(fromRoomId);
        for (int row = 0; row < room.getRows(); row++) {
            for (int column = 0; column < room.getColumns(); column++) {
                Cell cell = room.getCell(new Position(row, column));
                Door door = cell.getDoor();
                if (door != null && door.getDestinationRoomId() == toRoomId) {
                    return door;
                }
            }
        }
        return null;
    }
}
