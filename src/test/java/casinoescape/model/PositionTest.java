package casinoescape.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PositionTest {
    @Test
    void positionStoresCoordinates() {
        Position position = new Position(2, 3);

        assertEquals(2, position.getRow());
        assertEquals(3, position.getColumn());
    }

    @Test
    void negativeCoordinatesAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Position(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> new Position(0, -1));
    }

    @Test
    void positionsWithSameCoordinatesAreEqual() {
        Position first = new Position(1, 2);
        Position second = new Position(1, 2);
        Position third = new Position(2, 1);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, third);
    }
}
