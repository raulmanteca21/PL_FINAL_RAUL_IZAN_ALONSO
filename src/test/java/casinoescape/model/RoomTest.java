package casinoescape.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoomTest {
    @Test
    void roomStoresIdentityAndDimensions() {
        Room room = new Room(1, "Hall", 7, 7);

        assertEquals(1, room.getId());
        assertEquals("Hall", room.getName());
        assertEquals(7, room.getRows());
        assertEquals(7, room.getColumns());
    }

    @Test
    void roomStartsFilledWithIndependentEmptyCells() {
        Room room = new Room(1, "Hall", 2, 2);
        Position first = new Position(0, 0);
        Position second = new Position(0, 1);

        assertEquals(CellType.EMPTY, room.getCell(first).getType());
        assertEquals(CellType.EMPTY, room.getCell(second).getType());
        assertNotSame(room.getCell(first), room.getCell(second));
    }

    @Test
    void setCellAndSetCellTypeUpdateRoom() {
        Room room = new Room(1, "Hall", 7, 7);
        Position position = new Position(3, 4);

        room.setCell(position, new Cell(CellType.DOOR, "Sala 2"));

        assertEquals(CellType.DOOR, room.getCell(position).getType());
        assertEquals("Sala 2", room.getCell(position).getLabel());

        room.setCellType(position, CellType.OBSTACLE);

        assertEquals(CellType.OBSTACLE, room.getCell(position).getType());
    }

    @Test
    void isInsideValidatesPositions() {
        Room room = new Room(1, "Hall", 7, 7);

        assertTrue(room.isInside(new Position(0, 0)));
        assertTrue(room.isInside(new Position(6, 6)));
        assertFalse(room.isInside(new Position(7, 0)));
        assertFalse(room.isInside(null));
    }

    @Test
    void isWalkableUsesCellType() {
        Room room = new Room(1, "Hall", 7, 7);
        Position obstacle = new Position(1, 1);
        Position door = new Position(1, 2);

        room.setCellType(obstacle, CellType.OBSTACLE);
        room.setCellType(door, CellType.DOOR);

        assertFalse(room.isWalkable(obstacle));
        assertTrue(room.isWalkable(door));
    }

    @Test
    void invalidRoomArgumentsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Room(0, "Hall", 7, 7));
        assertThrows(IllegalArgumentException.class, () -> new Room(1, "", 7, 7));
        assertThrows(IllegalArgumentException.class, () -> new Room(1, "Hall", 0, 7));
    }

    @Test
    void positionOutsideRoomThrowsWhenAccessingCell() {
        Room room = new Room(1, "Hall", 7, 7);

        assertThrows(IndexOutOfBoundsException.class, () -> room.getCell(new Position(7, 0)));
        assertThrows(IndexOutOfBoundsException.class, () -> room.setCell(new Position(0, 7), Cell.empty()));
    }

    @Test
    void nullCellIsRejected() {
        Room room = new Room(1, "Hall", 7, 7);

        assertThrows(IllegalArgumentException.class, () -> room.setCell(new Position(0, 0), null));
    }
}
