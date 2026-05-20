package casinoescape.movement;

import casinoescape.model.CellType;
import casinoescape.model.Player;
import casinoescape.model.Position;
import casinoescape.model.Room;
import casinoescape.structures.MyLinkedList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReachableCellsCalculatorTest {
    private final ReachableCellsCalculator calculator = new ReachableCellsCalculator();

    @Test
    void speedOneReachesOnlyOrthogonalCells() {
        Room room = openRoom(5, 5, new Position(2, 2));

        MyLinkedList<Position> reachable = calculator.calculate(room, new Position(2, 2), 1);

        assertEquals(4, reachable.size());
        assertContains(reachable, new Position(1, 2));
        assertContains(reachable, new Position(3, 2));
        assertContains(reachable, new Position(2, 1));
        assertContains(reachable, new Position(2, 3));
        assertFalse(reachable.contains(new Position(3, 3)));
    }

    @Test
    void speedGreaterThanOneUsesBfs() {
        Room room = openRoom(5, 5, new Position(2, 2));

        MyLinkedList<Position> reachable = calculator.calculate(room, new Position(2, 2), 2);

        assertContains(reachable, new Position(0, 2));
        assertContains(reachable, new Position(4, 2));
        assertContains(reachable, new Position(2, 0));
        assertContains(reachable, new Position(2, 4));
        assertContains(reachable, new Position(1, 1));
        assertContains(reachable, new Position(3, 3));
    }

    @Test
    void obstaclesReduceReachableCells() {
        Room room = openRoom(5, 5, new Position(2, 2));
        room.setCellType(new Position(1, 2), CellType.OBSTACLE);
        room.setCellType(new Position(2, 1), CellType.OBSTACLE);

        MyLinkedList<Position> reachable = calculator.calculate(room, new Position(2, 2), 1);

        assertEquals(2, reachable.size());
        assertFalse(reachable.contains(new Position(1, 2)));
        assertFalse(reachable.contains(new Position(2, 1)));
        assertContains(reachable, new Position(3, 2));
        assertContains(reachable, new Position(2, 3));
    }

    @Test
    void cannotTraverseObstacle() {
        Room room = openRoom(5, 5, new Position(2, 1));
        room.setCellType(new Position(2, 2), CellType.OBSTACLE);

        MyLinkedList<Position> reachable = calculator.calculate(room, new Position(2, 1), 2);

        assertFalse(reachable.contains(new Position(2, 2)));
        assertFalse(reachable.contains(new Position(2, 3)));
    }

    @Test
    void nonWalkableCellsAreNotReachable() {
        Room room = openRoom(5, 5, new Position(2, 2));
        room.setCellType(new Position(1, 2), CellType.ENEMY);
        room.setCellType(new Position(2, 1), CellType.NPC);

        MyLinkedList<Position> reachable = calculator.calculate(room, new Position(2, 2), 1);

        assertFalse(reachable.contains(new Position(1, 2)));
        assertFalse(reachable.contains(new Position(2, 1)));
    }

    @Test
    void positionsOutsideMatrixAreNeverReached() {
        Room room = openRoom(3, 3, new Position(0, 0));

        MyLinkedList<Position> reachable = calculator.calculate(room, new Position(0, 0), 1);

        assertEquals(2, reachable.size());
        assertContains(reachable, new Position(1, 0));
        assertContains(reachable, new Position(0, 1));
    }

    @Test
    void invalidArgumentsAreRejected() {
        Room room = openRoom(3, 3, new Position(0, 0));

        assertThrows(IllegalArgumentException.class, () -> calculator.calculate(null, new Position(0, 0), 1));
        assertThrows(IllegalArgumentException.class, () -> calculator.calculate(room, null, 1));
        assertThrows(IllegalArgumentException.class, () -> calculator.calculate(room, new Position(0, 0), -1));
        assertThrows(IllegalArgumentException.class, () -> calculator.calculate(room, new Position(3, 0), 1));
    }

    private Room openRoom(int rows, int columns, Position playerPosition) {
        Room room = new Room(1, "Test", rows, columns);
        room.setCellType(playerPosition, CellType.PLAYER);
        return room;
    }

    private void assertContains(MyLinkedList<Position> positions, Position position) {
        assertTrue(positions.contains(position));
    }
}
