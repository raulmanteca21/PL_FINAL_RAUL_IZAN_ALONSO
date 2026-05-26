package casinoescape.movement;

import casinoescape.model.CasinoMap;
import casinoescape.model.CellType;
import casinoescape.model.Player;
import casinoescape.model.Position;
import casinoescape.model.Room;
import casinoescape.structures.MyLinkedList;
import casinoescape.structures.MyMatrix;
import casinoescape.structures.MyQueue;
import casinoescape.structures.MyStack;

public class PathFinder {
    private static final Direction[] DIRECTIONS = {
            Direction.UP,
            Direction.DOWN,
            Direction.LEFT,
            Direction.RIGHT
    };

    public ShortestPathInfo calculatePathToExit(CasinoMap map, Player player, boolean hasTreasuryKey) {
        requireMap(map);
        requirePlayer(player);

        int currentRoomId = player.getCurrentRoomId();
        MyLinkedList<Integer> roomPath = findRoomPathToExit(map, currentRoomId, hasTreasuryKey);
        if (roomPath.isEmpty()) {
            return new ShortestPathInfo(currentRoomId, CasinoMap.EXIT_ROOM_ID, roomPath,
                    ShortestPathInfo.NO_DISTANCE, ShortestPathInfo.NO_RECOMMENDED_ROOM,
                    null, new MyLinkedList<>(), ShortestPathInfo.NO_DISTANCE, false);
        }

        boolean alreadyInExitRoom = currentRoomId == CasinoMap.EXIT_ROOM_ID;
        int recommendedNextRoomId = alreadyInExitRoom
                ? ShortestPathInfo.NO_RECOMMENDED_ROOM
                : roomPath.get(1);
        Position targetPosition = alreadyInExitRoom
                ? map.getFirstCellPositionOfType(currentRoomId, CellType.EXIT)
                : map.getDoorPositionTo(currentRoomId, recommendedNextRoomId);
        MyLinkedList<Position> cellPath = findCellPath(map.getRoom(currentRoomId), player.getPosition(), targetPosition);
        int cellDistance = cellPath.isEmpty() ? ShortestPathInfo.NO_DISTANCE : cellPath.size() - 1;

        return new ShortestPathInfo(currentRoomId, CasinoMap.EXIT_ROOM_ID, roomPath,
                roomPath.size() - 1, recommendedNextRoomId, targetPosition, cellPath,
                cellDistance, alreadyInExitRoom);
    }

    public MyLinkedList<Integer> findRoomPathToExit(CasinoMap map, int currentRoomId, boolean hasTreasuryKey) {
        requireMap(map);
        map.getRoom(currentRoomId);

        MyQueue<RoomPathNode> pending = new MyQueue<>();
        MyLinkedList<Integer> visited = new MyLinkedList<>();

        pending.enqueue(new RoomPathNode(currentRoomId, null));
        visited.add(currentRoomId);

        while (!pending.isEmpty()) {
            RoomPathNode current = pending.dequeue();
            if (current.roomId == CasinoMap.EXIT_ROOM_ID) {
                return buildRoomPath(current);
            }

            MyLinkedList<Integer> neighbors = map.getConnectedRooms(current.roomId);
            for (int i = 0; i < neighbors.size(); i++) {
                int neighbor = neighbors.get(i);
                if (!visited.contains(neighbor) && map.canTransition(current.roomId, neighbor, hasTreasuryKey)) {
                    visited.add(neighbor);
                    pending.enqueue(new RoomPathNode(neighbor, current));
                }
            }
        }

        return new MyLinkedList<>();
    }

    public MyLinkedList<Position> findCellPath(Room room, Position start, Position goal) {
        requireRoom(room);
        requirePosition(start, "Start position is required");
        requirePosition(goal, "Goal position is required");
        if (!room.isInside(start) || !room.isInside(goal)) {
            throw new IllegalArgumentException("Path positions must be inside room");
        }
        if (start.equals(goal)) {
            MyLinkedList<Position> path = new MyLinkedList<>();
            path.add(start);
            return path;
        }
        MyQueue<CellPathNode> pending = new MyQueue<>();
        MyMatrix<Boolean> visited = new MyMatrix<>(room.getRows(), room.getColumns());

        pending.enqueue(new CellPathNode(start, null));
        visited.set(start.getRow(), start.getColumn(), Boolean.TRUE);

        while (!pending.isEmpty()) {
            CellPathNode current = pending.dequeue();
            if (current.position.equals(goal)) {
                return buildCellPath(current);
            }

            for (int i = 0; i < DIRECTIONS.length; i++) {
                Direction direction = DIRECTIONS[i];
                int nextRow = current.position.getRow() + direction.getRowDelta();
                int nextColumn = current.position.getColumn() + direction.getColumnDelta();
                if (!isInside(room, nextRow, nextColumn)) {
                    continue;
                }
                Position next = new Position(nextRow, nextColumn);
                if (canVisit(room, visited, next, goal)) {
                    visited.set(next.getRow(), next.getColumn(), Boolean.TRUE);
                    pending.enqueue(new CellPathNode(next, current));
                }
            }
        }

        return new MyLinkedList<>();
    }

    private boolean canVisit(Room room, MyMatrix<Boolean> visited, Position position) {
        return !Boolean.TRUE.equals(visited.get(position.getRow(), position.getColumn()))
                && room.isWalkable(position);
    }

    private boolean isInside(Room room, int row, int column) {
        return row >= 0 && row < room.getRows() && column >= 0 && column < room.getColumns();
    }

    private MyLinkedList<Integer> buildRoomPath(RoomPathNode end) {
        MyStack<Integer> reversed = new MyStack<>();
        RoomPathNode current = end;
        while (current != null) {
            reversed.push(current.roomId);
            current = current.previous;
        }

        MyLinkedList<Integer> path = new MyLinkedList<>();
        while (!reversed.isEmpty()) {
            path.add(reversed.pop());
        }
        return path;
    }

    private MyLinkedList<Position> buildCellPath(CellPathNode end) {
        MyStack<Position> reversed = new MyStack<>();
        CellPathNode current = end;
        while (current != null) {
            reversed.push(current.position);
            current = current.previous;
        }

        MyLinkedList<Position> path = new MyLinkedList<>();
        while (!reversed.isEmpty()) {
            path.add(reversed.pop());
        }
        return path;
    }

    private void requireMap(CasinoMap map) {
        if (map == null) {
            throw new IllegalArgumentException("Map is required");
        }
    }

    private void requireRoom(Room room) {
        if (room == null) {
            throw new IllegalArgumentException("Room is required");
        }
    }

    private void requirePlayer(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player is required");
        }
    }

    private void requirePosition(Position position, String message) {
        if (position == null) {
            throw new IllegalArgumentException(message);
        }
    }

    private static class RoomPathNode {
        private final int roomId;
        private final RoomPathNode previous;

        private RoomPathNode(int roomId, RoomPathNode previous) {
            this.roomId = roomId;
            this.previous = previous;
        }
    }

    private static class CellPathNode {
        private final Position position;
        private final CellPathNode previous;

        private CellPathNode(Position position, CellPathNode previous) {
            this.position = position;
            this.previous = previous;
        }
    }
}
