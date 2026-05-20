package casinoescape.movement;

import casinoescape.model.CellType;
import casinoescape.model.Player;
import casinoescape.model.Position;
import casinoescape.model.Room;

public class MovementService {
    private final ReachableCellsCalculator reachableCellsCalculator;

    public MovementService() {
        this(new ReachableCellsCalculator());
    }

    public MovementService(ReachableCellsCalculator reachableCellsCalculator) {
        if (reachableCellsCalculator == null) {
            throw new IllegalArgumentException("Reachable cells calculator is required");
        }
        this.reachableCellsCalculator = reachableCellsCalculator;
    }

    public boolean canMove(Room room, Player player, Position destination) {
        validateRoomAndPlayer(room, player);
        return reachableCellsCalculator.isReachable(room, player.getPosition(), destination, player.getMovementPoints());
    }

    public void movePlayer(Room room, Player player, Position destination) {
        validateRoomAndPlayer(room, player);
        if (!canMove(room, player, destination)) {
            throw new IllegalArgumentException("Destination is not reachable");
        }

        Position origin = player.getPosition();
        room.setCellType(origin, CellType.EMPTY);
        player.setPosition(destination);
        room.setCellType(destination, CellType.PLAYER);
    }

    public Position calculateLineDestination(Room room, Position start, Direction direction) {
        if (room == null) {
            throw new IllegalArgumentException("Room is required");
        }
        if (start == null) {
            throw new IllegalArgumentException("Start position is required");
        }
        if (direction == null) {
            throw new IllegalArgumentException("Direction is required");
        }
        if (!room.isInside(start)) {
            throw new IllegalArgumentException("Start position is outside room");
        }

        Position current = start;
        while (true) {
            int nextRow = current.getRow() + direction.getRowDelta();
            int nextColumn = current.getColumn() + direction.getColumnDelta();
            if (nextRow < 0 || nextRow >= room.getRows() || nextColumn < 0 || nextColumn >= room.getColumns()) {
                return current;
            }
            Position next = new Position(nextRow, nextColumn);
            if (!room.isWalkable(next) || room.getCell(next).getType() == CellType.DOOR) {
                return current;
            }
            current = next;
        }
    }

    public void movePlayerInLine(Room room, Player player, Direction direction) {
        validateRoomAndPlayer(room, player);
        Position destination = calculateLineDestination(room, player.getPosition(), direction);
        if (destination.equals(player.getPosition())) {
            throw new IllegalArgumentException("No line movement is possible");
        }
        Position origin = player.getPosition();
        room.setCellType(origin, CellType.EMPTY);
        player.setPosition(destination);
        room.setCellType(destination, CellType.PLAYER);
    }

    private void validateRoomAndPlayer(Room room, Player player) {
        if (room == null) {
            throw new IllegalArgumentException("Room is required");
        }
        if (player == null) {
            throw new IllegalArgumentException("Player is required");
        }
        if (!room.isInside(player.getPosition())) {
            throw new IllegalArgumentException("Player position is outside room");
        }
    }
}
