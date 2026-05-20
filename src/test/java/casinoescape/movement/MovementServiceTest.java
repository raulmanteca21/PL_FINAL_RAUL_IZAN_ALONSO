package casinoescape.movement;

import casinoescape.model.Cell;
import casinoescape.model.CellType;
import casinoescape.model.Door;
import casinoescape.model.Player;
import casinoescape.model.Position;
import casinoescape.model.Room;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MovementServiceTest {
    private final MovementService movementService = new MovementService();

    @Test
    void playerCanMoveToReachableCell() {
        Position start = new Position(2, 2);
        Room room = openRoom(5, 5, start);
        Player player = playerAt(start, 2);
        Position destination = new Position(2, 4);

        movementService.movePlayer(room, player, destination);

        assertEquals(destination, player.getPosition());
        assertEquals(CellType.EMPTY, room.getCell(start).getType());
        assertEquals(CellType.PLAYER, room.getCell(destination).getType());
    }

    @Test
    void playerCannotMoveToUnreachableCell() {
        Position start = new Position(2, 2);
        Room room = openRoom(5, 5, start);
        Player player = playerAt(start, 1);

        assertFalse(movementService.canMove(room, player, new Position(2, 4)));
        assertThrows(IllegalArgumentException.class, () -> movementService.movePlayer(room, player, new Position(2, 4)));
    }

    @Test
    void playerCannotMoveDiagonallyWithOneMovementPoint() {
        Position start = new Position(2, 2);
        Room room = openRoom(5, 5, start);
        Player player = playerAt(start, 1);

        assertFalse(movementService.canMove(room, player, new Position(3, 3)));
    }

    @Test
    void playerCannotMoveToObstacle() {
        Position start = new Position(2, 2);
        Room room = openRoom(5, 5, start);
        Player player = playerAt(start, 2);
        Position obstacle = new Position(2, 3);
        room.setCellType(obstacle, CellType.OBSTACLE);

        assertFalse(movementService.canMove(room, player, obstacle));
    }

    @Test
    void lineMovementStopsBeforeObstacle() {
        Position start = new Position(2, 1);
        Room room = openRoom(5, 5, start);
        room.setCellType(new Position(2, 3), CellType.OBSTACLE);

        Position destination = movementService.calculateLineDestination(room, start, Direction.RIGHT);

        assertEquals(new Position(2, 2), destination);
    }

    @Test
    void lineMovementStopsBeforeDoor() {
        Position start = new Position(2, 1);
        Room room = openRoom(5, 5, start);
        room.setCell(new Position(2, 3), new Cell(new Door(2), "Puerta"));

        Position destination = movementService.calculateLineDestination(room, start, Direction.RIGHT);

        assertEquals(new Position(2, 2), destination);
    }

    @Test
    void lineMovementStopsAtRoomBoundary() {
        Position start = new Position(2, 2);
        Room room = openRoom(5, 5, start);

        Position destination = movementService.calculateLineDestination(room, start, Direction.RIGHT);

        assertEquals(new Position(2, 4), destination);
    }

    @Test
    void lineMovementUpdatesPlayerWhenPossible() {
        Position start = new Position(2, 1);
        Room room = openRoom(5, 5, start);
        Player player = playerAt(start, 1);
        room.setCellType(new Position(2, 3), CellType.OBSTACLE);

        movementService.movePlayerInLine(room, player, Direction.RIGHT);

        assertEquals(new Position(2, 2), player.getPosition());
        assertEquals(CellType.EMPTY, room.getCell(start).getType());
        assertEquals(CellType.PLAYER, room.getCell(new Position(2, 2)).getType());
    }

    @Test
    void lineMovementFailsWhenBlockedImmediately() {
        Position start = new Position(2, 1);
        Room room = openRoom(5, 5, start);
        Player player = playerAt(start, 1);
        room.setCellType(new Position(2, 2), CellType.OBSTACLE);

        assertThrows(IllegalArgumentException.class, () -> movementService.movePlayerInLine(room, player, Direction.RIGHT));
    }

    @Test
    void invalidArgumentsAreRejected() {
        Position start = new Position(2, 2);
        Room room = openRoom(5, 5, start);
        Player player = playerAt(start, 1);

        assertThrows(IllegalArgumentException.class, () -> movementService.canMove(null, player, new Position(2, 3)));
        assertThrows(IllegalArgumentException.class, () -> movementService.canMove(room, null, new Position(2, 3)));
        assertThrows(IllegalArgumentException.class, () -> movementService.calculateLineDestination(room, start, null));
    }

    private Room openRoom(int rows, int columns, Position playerPosition) {
        Room room = new Room(1, "Test", rows, columns);
        room.setCellType(playerPosition, CellType.PLAYER);
        return room;
    }

    private Player playerAt(Position position, int movementPoints) {
        return new Player(100, 10, 5, movementPoints, 1, position);
    }
}
