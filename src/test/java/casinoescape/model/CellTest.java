package casinoescape.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CellTest {
    @Test
    void cellStoresTypeAndLabel() {
        Cell cell = new Cell(CellType.DOOR, "Sala 2");

        assertEquals(CellType.DOOR, cell.getType());
        assertEquals("Sala 2", cell.getLabel());
    }

    @Test
    void emptyFactoryCreatesEmptyCell() {
        Cell cell = Cell.empty();

        assertEquals(CellType.EMPTY, cell.getType());
        assertTrue(cell.isWalkable());
        assertFalse(cell.isInteractive());
    }

    @Test
    void blockingCellsAreNotWalkable() {
        assertFalse(new Cell(CellType.OBSTACLE).isWalkable());
        assertFalse(new Cell(CellType.PLAYER).isWalkable());
        assertFalse(new Cell(CellType.ENEMY).isWalkable());
        assertFalse(new Cell(CellType.NPC).isWalkable());
    }

    @Test
    void interactiveCellsAreDetected() {
        assertTrue(new Cell(CellType.ITEM).isInteractive());
        assertTrue(new Cell(CellType.DOOR).isInteractive());
        assertTrue(new Cell(CellType.NPC).isInteractive());
        assertTrue(new Cell(CellType.TRAP).isInteractive());
        assertTrue(new Cell(CellType.SHOP).isInteractive());
        assertTrue(new Cell(CellType.EXIT).isInteractive());
        assertTrue(new Cell(CellType.MINIGAME).isInteractive());
    }

    @Test
    void doorCellStoresDoorData() {
        Door door = new Door(2);
        Cell cell = new Cell(door, "Puerta");

        assertEquals(CellType.DOOR, cell.getType());
        assertEquals(door, cell.getDoor());
        assertEquals("Puerta", cell.getLabel());
    }

    @Test
    void changingDoorCellToAnotherTypeRemovesDoorData() {
        Cell cell = new Cell(new Door(2), "Puerta");

        cell.setType(CellType.EMPTY);

        assertEquals(CellType.EMPTY, cell.getType());
        assertEquals(null, cell.getDoor());
    }

    @Test
    void nullTypeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Cell(null));
    }

    @Test
    void nullLabelBecomesEmptyText() {
        Cell cell = new Cell(CellType.EMPTY, null);

        assertEquals("", cell.getLabel());
    }
}
