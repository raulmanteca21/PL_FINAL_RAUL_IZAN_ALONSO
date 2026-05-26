package casinoescape.movement;

import casinoescape.game.CasinoMapBuilder;
import casinoescape.model.CasinoMap;
import casinoescape.model.Cell;
import casinoescape.model.CellType;
import casinoescape.model.Door;
import casinoescape.model.Player;
import casinoescape.model.Position;
import casinoescape.model.Room;
import casinoescape.structures.MyLinkedList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PathFinderTest {
    private final PathFinder pathFinder = new PathFinder();

    @Test
    void calculatesMinimumRoomPathFromRoomOneToExit() {
        CasinoMap map = new CasinoMapBuilder().buildBaseMap();

        MyLinkedList<Integer> path = pathFinder.findRoomPathToExit(map, 1, false);

        assertEquals(5, path.size());
        assertEquals(1, path.get(0));
        assertEquals(CasinoMap.EXIT_ROOM_ID, path.get(path.size() - 1));
        assertEquals(4, path.size() - 1);
        assertTrue(path.get(1) == 2 || path.get(1) == 4);
    }

    @Test
    void calculatesRoomPathFromRoomFiveAndSeven() {
        CasinoMap map = new CasinoMapBuilder().buildBaseMap();

        MyLinkedList<Integer> fromFive = pathFinder.findRoomPathToExit(map, 5, false);
        MyLinkedList<Integer> fromSeven = pathFinder.findRoomPathToExit(map, 7, false);

        assertEquals(3, fromFive.size());
        assertEquals(5, fromFive.get(0));
        assertEquals(7, fromFive.get(1));
        assertEquals(8, fromFive.get(2));
        assertEquals(2, fromSeven.size());
        assertEquals(7, fromSeven.get(0));
        assertEquals(8, fromSeven.get(1));
    }

    @Test
    void roomEightIsAlreadyExitRoom() {
        CasinoMap map = new CasinoMapBuilder().buildBaseMap();
        Player player = new Player(100, 10, 5, 3, 8, new Position(0, 4));

        ShortestPathInfo info = pathFinder.calculatePathToExit(map, player, false);

        assertTrue(info.isAlreadyInExitRoom());
        assertEquals(0, info.getRoomDistance());
        assertEquals(ShortestPathInfo.NO_RECOMMENDED_ROOM, info.getRecommendedNextRoomId());
        assertEquals(CasinoMapBuilder.EXIT_POSITION, info.getRecommendedTargetPosition());
        assertEquals(1, info.getCellDistance());
    }

    @Test
    void recommendsNextRoomDoorAndMatrixDistanceFromRoomFive() {
        CasinoMap map = new CasinoMapBuilder().buildBaseMap();
        Player player = new Player(100, 10, 5, 3, 5, new Position(3, 3));

        ShortestPathInfo info = pathFinder.calculatePathToExit(map, player, false);

        assertEquals(2, info.getRoomDistance());
        assertEquals(7, info.getRecommendedNextRoomId());
        assertEquals(new Position(3, 6), info.getRecommendedTargetPosition());
        assertEquals(5, info.getCellDistance());
        assertEquals(player.getPosition(), info.getCellPath().get(0));
        assertEquals(new Position(3, 6), info.getCellPath().get(info.getCellPath().size() - 1));
        assertFalse(info.getCellPath().contains(new Position(3, 5)));
    }

    @Test
    void matrixBfsDoesNotCrossObstaclesAndDoesNotUseDiagonals() {
        Room room = new Room(1, "Test", 5, 5);
        Position start = new Position(2, 1);
        Position goal = new Position(2, 3);
        room.setCellType(start, CellType.PLAYER);
        room.setCell(goal, new Cell(new Door(2), "Puerta"));
        room.setCellType(new Position(2, 2), CellType.OBSTACLE);

        MyLinkedList<Position> path = pathFinder.findCellPath(room, start, goal);

        assertEquals(5, path.size());
        assertEquals(4, path.size() - 1);
        assertFalse(path.contains(new Position(2, 2)));
        assertOrthogonalPath(path);
    }

    @Test
    void matrixBfsReturnsEmptyPathWhenTargetIsUnreachable() {
        Room room = new Room(1, "Test", 3, 3);
        Position start = new Position(0, 0);
        Position goal = new Position(1, 1);
        room.setCellType(start, CellType.PLAYER);
        room.setCell(goal, new Cell(new Door(2), "Puerta"));
        room.setCellType(new Position(0, 1), CellType.OBSTACLE);
        room.setCellType(new Position(1, 0), CellType.OBSTACLE);
        room.setCellType(new Position(1, 2), CellType.OBSTACLE);
        room.setCellType(new Position(2, 1), CellType.OBSTACLE);

        MyLinkedList<Position> path = pathFinder.findCellPath(room, start, goal);

        assertTrue(path.isEmpty());
    }

    @Test
    void playerAlreadyOnTargetHasZeroCellDistance() {
        Room room = new Room(1, "Test", 3, 3);
        Position start = new Position(1, 1);
        room.setCellType(start, CellType.PLAYER);

        MyLinkedList<Position> path = pathFinder.findCellPath(room, start, start);

        assertEquals(1, path.size());
        assertEquals(start, path.get(0));
    }

    @Test
    void matrixBfsDoesNotReturnPathToNonWalkableTarget() {
        Room room = new Room(1, "Test", 3, 3);
        Position start = new Position(0, 0);
        Position goal = new Position(0, 1);
        room.setCellType(start, CellType.PLAYER);
        room.setCellType(goal, CellType.NPC);

        MyLinkedList<Position> path = pathFinder.findCellPath(room, start, goal);

        assertTrue(path.isEmpty());
    }

    @Test
    void invalidInputsAreRejected() {
        CasinoMap map = new CasinoMapBuilder().buildBaseMap();

        assertThrows(IllegalArgumentException.class, () -> pathFinder.findRoomPathToExit(null, 1, false));
        assertThrows(IllegalArgumentException.class, () -> pathFinder.findRoomPathToExit(map, 99, false));
        assertThrows(IllegalArgumentException.class, () -> pathFinder.findCellPath(null, new Position(0, 0), new Position(0, 1)));
    }

    private void assertOrthogonalPath(MyLinkedList<Position> path) {
        for (int i = 1; i < path.size(); i++) {
            Position previous = path.get(i - 1);
            Position current = path.get(i);
            int rowDistance = absolute(previous.getRow() - current.getRow());
            int columnDistance = absolute(previous.getColumn() - current.getColumn());
            assertEquals(1, rowDistance + columnDistance);
        }
    }

    private int absolute(int value) {
        return value < 0 ? -value : value;
    }
}
