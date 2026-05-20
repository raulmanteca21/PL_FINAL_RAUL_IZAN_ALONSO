package casinoescape.combat;

import casinoescape.model.Enemy;
import casinoescape.model.Player;
import casinoescape.model.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombatServiceTest {
    private final CombatService combatService = new CombatService();

    @Test
    void validAttackReducesEnemyHealth() {
        Player player = playerAt(new Position(3, 3));
        Enemy enemy = enemyAt(new Position(3, 4), 30, 5, "");

        CombatResult result = combatService.playerAttacksEnemy(player, enemy, 0.75);

        assertEquals(10, result.getDamageDealt());
        assertEquals(20, enemy.getCurrentHealth());
        assertFalse(result.isDefenderDied());
    }

    @Test
    void attackWithEnoughDefenseDealsZeroDamage() {
        Player player = new Player(100, 4, 5, 3, 1, new Position(3, 3));
        Enemy enemy = enemyAt(new Position(3, 4), 30, 20, "");

        CombatResult result = combatService.playerAttacksEnemy(player, enemy, 0.5);

        assertEquals(0, result.getDamageDealt());
        assertEquals(30, enemy.getCurrentHealth());
    }

    @Test
    void enemyHealthNeverGoesBelowZero() {
        Player player = new Player(100, 50, 5, 3, 1, new Position(3, 3));
        Enemy enemy = enemyAt(new Position(3, 4), 5, 0, "");

        CombatResult result = combatService.playerAttacksEnemy(player, enemy, 1.0);

        assertEquals(0, enemy.getCurrentHealth());
        assertTrue(result.isDefenderDied());
    }

    @Test
    void nonAdjacentAttackIsRejected() {
        Player player = playerAt(new Position(3, 3));
        Enemy enemy = enemyAt(new Position(3, 5), 30, 5, "");

        assertThrows(IllegalArgumentException.class, () -> combatService.playerAttacksEnemy(player, enemy, 0.75));
        assertEquals(30, enemy.getCurrentHealth());
    }

    @Test
    void diagonalAttackIsRejected() {
        Player player = playerAt(new Position(3, 3));
        Enemy enemy = enemyAt(new Position(2, 2), 30, 5, "");

        assertFalse(combatService.areAdjacent(player.getPosition(), enemy.getPosition()));
        assertThrows(IllegalArgumentException.class, () -> combatService.playerAttacksEnemy(player, enemy, 0.75));
    }

    @Test
    void allOrthogonalDirectionsAreAdjacent() {
        Position center = new Position(3, 3);

        assertTrue(combatService.areAdjacent(center, new Position(2, 3)));
        assertTrue(combatService.areAdjacent(center, new Position(4, 3)));
        assertTrue(combatService.areAdjacent(center, new Position(3, 2)));
        assertTrue(combatService.areAdjacent(center, new Position(3, 4)));
    }

    @Test
    void deadEnemyGrantsChipsOnceAndStoresDropText() {
        Player player = playerAt(new Position(3, 3));
        Enemy enemy = enemyAt(new Position(3, 4), 5, 0, "Traje con escudo");

        CombatResult result = combatService.playerAttacksEnemy(player, enemy, 1.0);

        assertFalse(enemy.isAlive());
        assertTrue(enemy.isRewardClaimed());
        assertEquals(7, player.getChips());
        assertEquals(7, result.getChipsAwarded());
        assertEquals("Traje con escudo", result.getDroppedItemName());
        assertThrows(IllegalStateException.class, () -> combatService.playerAttacksEnemy(player, enemy, 1.0));
        assertEquals(7, player.getChips());
    }

    @Test
    void blackjackDealerCanDropShieldSuit() {
        Enemy dealer = new Enemy("Crupier de Blackjack", 5, 5, 0, new Position(3, 4), 7, "Traje con escudo");
        Player player = playerAt(new Position(3, 3));

        CombatResult result = combatService.playerAttacksEnemy(player, dealer, 1.0);

        assertTrue(result.isDefenderDied());
        assertEquals("Traje con escudo", result.getDroppedItemName());
    }

    @Test
    void enemyCanAttackAdjacentPlayer() {
        Player player = playerAt(new Position(3, 3));
        Enemy enemy = new Enemy("Enemy", 20, 10, 2, new Position(3, 4), 7, "");

        CombatResult result = combatService.enemyAttacksPlayer(enemy, player, 0.75);

        assertEquals(10, result.getDamageDealt());
        assertEquals(90, player.getCurrentHealth());
    }

    @Test
    void playerHealthNeverGoesBelowZero() {
        Player player = playerAt(new Position(3, 3));
        Enemy enemy = new Enemy("Enemy", 20, 100, 2, new Position(3, 4), 7, "");

        CombatResult result = combatService.enemyAttacksPlayer(enemy, player, 1.0);

        assertEquals(0, player.getCurrentHealth());
        assertTrue(result.isDefenderDied());
    }

    @Test
    void invalidArgumentsAreRejected() {
        Player player = playerAt(new Position(3, 3));
        Enemy enemy = enemyAt(new Position(3, 4), 20, 2, "");

        assertThrows(IllegalArgumentException.class, () -> combatService.playerAttacksEnemy(null, enemy, 0.5));
        assertThrows(IllegalArgumentException.class, () -> combatService.playerAttacksEnemy(player, null, 0.5));
        assertThrows(IllegalArgumentException.class, () -> combatService.enemyAttacksPlayer(null, player, 0.5));
        assertThrows(IllegalArgumentException.class, () -> combatService.enemyAttacksPlayer(enemy, null, 0.5));
    }

    private Player playerAt(Position position) {
        return new Player(100, 10, 5, 3, 1, position);
    }

    private Enemy enemyAt(Position position, int health, int defense, String dropName) {
        return new Enemy("Enemy", health, 5, defense, position, 7, dropName);
    }
}
