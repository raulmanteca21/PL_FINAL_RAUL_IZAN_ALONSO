package casinoescape.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnemyTest {
    @Test
    void enemyStoresInitialState() {
        Position position = new Position(2, 3);
        Enemy enemy = new Enemy("Crupier", 30, 8, 3, position, 5, "Traje con escudo");

        assertEquals("Crupier", enemy.getName());
        assertEquals(30, enemy.getMaxHealth());
        assertEquals(30, enemy.getCurrentHealth());
        assertEquals(8, enemy.getAttack());
        assertEquals(3, enemy.getDefense());
        assertEquals(position, enemy.getPosition());
        assertEquals(5, enemy.getChipReward());
        assertEquals("Traje con escudo", enemy.getDropName());
        assertTrue(enemy.isAlive());
        assertFalse(enemy.isRewardClaimed());
    }

    @Test
    void healthIsClampedBetweenZeroAndMax() {
        Enemy enemy = enemy();

        enemy.setCurrentHealth(100);
        assertEquals(20, enemy.getCurrentHealth());

        enemy.setCurrentHealth(-5);
        assertEquals(0, enemy.getCurrentHealth());
        assertFalse(enemy.isAlive());
    }

    @Test
    void rewardCanBeMarkedAsClaimed() {
        Enemy enemy = enemy();

        enemy.markRewardClaimed();

        assertTrue(enemy.isRewardClaimed());
    }

    @Test
    void invalidArgumentsAreRejected() {
        Position position = new Position(1, 1);

        assertThrows(IllegalArgumentException.class, () -> new Enemy("", 20, 5, 2, position, 3, ""));
        assertThrows(IllegalArgumentException.class, () -> new Enemy("Enemy", 0, 5, 2, position, 3, ""));
        assertThrows(IllegalArgumentException.class, () -> new Enemy("Enemy", 20, -1, 2, position, 3, ""));
        assertThrows(IllegalArgumentException.class, () -> new Enemy("Enemy", 20, 5, -1, position, 3, ""));
        assertThrows(IllegalArgumentException.class, () -> new Enemy("Enemy", 20, 5, 2, null, 3, ""));
        assertThrows(IllegalArgumentException.class, () -> new Enemy("Enemy", 20, 5, 2, position, -1, ""));
    }

    private Enemy enemy() {
        return new Enemy("Enemy", 20, 5, 2, new Position(1, 1), 3, "");
    }
}
