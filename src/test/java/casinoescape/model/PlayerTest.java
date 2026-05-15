package casinoescape.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerTest {
    @Test
    void playerStoresInitialState() {
        Position position = new Position(3, 3);
        Player player = new Player(100, 10, 5, 3, 1, position);

        assertEquals(100, player.getMaxHealth());
        assertEquals(100, player.getCurrentHealth());
        assertEquals(10, player.getAttack());
        assertEquals(5, player.getDefense());
        assertEquals(3, player.getMovementPoints());
        assertEquals(1, player.getCurrentRoomId());
        assertEquals(position, player.getPosition());
        assertEquals(0, player.getChips());
        assertFalse(player.isFriendRescued());
        assertTrue(player.isAlive());
    }

    @Test
    void healthIsClampedBetweenZeroAndMax() {
        Player player = new Player(100, 10, 5, 3, 1, new Position(3, 3));

        player.setCurrentHealth(150);
        assertEquals(100, player.getCurrentHealth());

        player.setCurrentHealth(-5);
        assertEquals(0, player.getCurrentHealth());
        assertFalse(player.isAlive());
    }

    @Test
    void statsCanBeUpdatedWithValidation() {
        Player player = new Player(100, 10, 5, 3, 1, new Position(3, 3));

        player.setAttack(12);
        player.setDefense(8);
        player.setMovementPoints(4);

        assertEquals(12, player.getAttack());
        assertEquals(8, player.getDefense());
        assertEquals(4, player.getMovementPoints());

        assertThrows(IllegalArgumentException.class, () -> player.setAttack(-1));
        assertThrows(IllegalArgumentException.class, () -> player.setDefense(-1));
        assertThrows(IllegalArgumentException.class, () -> player.setMovementPoints(0));
    }

    @Test
    void chipsCanBeAddedAndSpent() {
        Player player = new Player(100, 10, 5, 3, 1, new Position(3, 3));

        player.addChips(10);
        player.spendChips(4);

        assertEquals(6, player.getChips());
        assertThrows(IllegalArgumentException.class, () -> player.spendChips(7));
    }

    @Test
    void roomAndPositionCanBeUpdated() {
        Player player = new Player(100, 10, 5, 3, 1, new Position(3, 3));
        Position newPosition = new Position(1, 1);

        player.setCurrentRoomId(2);
        player.setPosition(newPosition);

        assertEquals(2, player.getCurrentRoomId());
        assertEquals(newPosition, player.getPosition());
    }

    @Test
    void friendCanBeRescued() {
        Player player = new Player(100, 10, 5, 3, 1, new Position(3, 3));

        player.rescueFriend();

        assertTrue(player.isFriendRescued());
    }

    @Test
    void invalidConstructorArgumentsAreRejected() {
        Position position = new Position(0, 0);

        assertThrows(IllegalArgumentException.class, () -> new Player(0, 10, 5, 3, 1, position));
        assertThrows(IllegalArgumentException.class, () -> new Player(100, -1, 5, 3, 1, position));
        assertThrows(IllegalArgumentException.class, () -> new Player(100, 10, -1, 3, 1, position));
        assertThrows(IllegalArgumentException.class, () -> new Player(100, 10, 5, 0, 1, position));
        assertThrows(IllegalArgumentException.class, () -> new Player(100, 10, 5, 3, 0, position));
        assertThrows(IllegalArgumentException.class, () -> new Player(100, 10, 5, 3, 1, null));
    }

    @Test
    void negativeChipAmountsAreRejected() {
        Player player = new Player(100, 10, 5, 3, 1, new Position(3, 3));

        assertThrows(IllegalArgumentException.class, () -> player.addChips(-1));
        assertThrows(IllegalArgumentException.class, () -> player.spendChips(-1));
    }
}
