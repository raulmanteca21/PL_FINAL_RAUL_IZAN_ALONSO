package casinoescape.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DoorTest {
    @Test
    void unlockedDoorAllowsTransitionWithoutKey() {
        Door door = new Door(2);

        assertEquals(2, door.getDestinationRoomId());
        assertFalse(door.isLocked());
        assertEquals("", door.getRequiredKeyName());
        assertTrue(door.canPass(false));
    }

    @Test
    void lockedDoorRequiresKey() {
        Door door = new Door(3, true, "Llave de Tesoreria");

        assertTrue(door.isLocked());
        assertEquals("Llave de Tesoreria", door.getRequiredKeyName());
        assertFalse(door.canPass(false));
        assertTrue(door.canPass(true));
    }

    @Test
    void invalidDoorArgumentsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Door(0));
        assertThrows(IllegalArgumentException.class, () -> new Door(3, true, ""));
    }
}
