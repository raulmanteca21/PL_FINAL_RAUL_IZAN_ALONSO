package casinoescape.game;

import casinoescape.items.Inventory;
import casinoescape.items.Shop;
import casinoescape.logging.GameLog;
import casinoescape.model.CellType;
import casinoescape.model.Enemy;
import casinoescape.model.Player;
import casinoescape.model.Position;
import casinoescape.movement.MovementService;
import casinoescape.movement.PathFinder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameEnemyPhaseTest {
    @Test
    void endTurnAdjacentEnemyAttacksPlayer() {
        FixedRandomGame game = new FixedRandomGame(30, 1.0);
        game.getCurrentRoom().removeItemById(CasinoMapBuilder.BROKEN_BOTTLE_ID);
        game.getCurrentRoom().addEnemy(new Enemy("TEST_ENEMY", "Enemigo de prueba", 10, 3, 0,
                new Position(3, 4), 0, ""));

        game.endTurn();

        assertEquals(Game.INITIAL_HEALTH - 6, game.getPlayer().getCurrentHealth());
        assertEquals(29, game.getTurnManager().getTurnsRemaining());
        assertTrue(game.getLog().getEntry(0).getMessage().contains("ataca al jugador"));
    }

    @Test
    void endTurnNonAdjacentEnemyMovesTowardsPlayer() {
        FixedRandomGame game = new FixedRandomGame(30, 1.0);
        Enemy enemy = new Enemy("TEST_ENEMY", "Enemigo de prueba", 10, 3, 0,
                new Position(5, 3), 0, "");
        game.getCurrentRoom().addEnemy(enemy);

        game.endTurn();

        assertEquals(new Position(4, 3), enemy.getPosition());
        assertEquals(CellType.ENEMY, game.getCurrentRoom().getCell(new Position(4, 3)).getType());
    }

    @Test
    void enemyAttackCanCauseDefeat() {
        FixedRandomGame game = new FixedRandomGame(30, 1.0);
        game.getCurrentRoom().removeItemById(CasinoMapBuilder.BROKEN_BOTTLE_ID);
        game.getCurrentRoom().addEnemy(new Enemy("LETHAL_ENEMY", "Enemigo letal", 10, 20, 0,
                new Position(3, 4), 0, ""));

        game.endTurn();

        assertEquals(0, game.getPlayer().getCurrentHealth());
        assertEquals(casinoescape.model.GameState.DEFEAT, game.getState());
    }

    private static class FixedRandomGame extends Game {
        private final double randomValue;

        private FixedRandomGame(int turnsRemaining, double randomValue) {
            super(
                    new CasinoMapBuilder().buildBaseMap(),
                    new Player(Game.INITIAL_HEALTH, Game.INITIAL_ATTACK, Game.INITIAL_DEFENSE,
                            Game.INITIAL_MOVEMENT, 1, new Position(3, 3)),
                    new Inventory(),
                    new TurnManager(turnsRemaining),
                    new MovementService(),
                    new PathFinder(),
                    Shop.createDefaultBarShop(),
                    new GameLog(),
                    Game.createWelcomeNpcForRestore(false),
                    Game.createBarSpecialNpcForRestore(false),
                    Game.createFriendNpcForRestore(false),
                    Game.createDangerousCompanionForRestore());
            this.randomValue = randomValue;
        }

        @Override
        protected double nextRandomValue() {
            return randomValue;
        }
    }
}
