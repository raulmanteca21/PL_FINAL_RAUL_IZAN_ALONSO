package casinoescape.game;

import casinoescape.combat.CombatResult;
import casinoescape.items.Effect;
import casinoescape.items.EffectType;
import casinoescape.items.Item;
import casinoescape.model.CellType;
import casinoescape.model.Enemy;
import casinoescape.model.Position;
import casinoescape.model.Room;
import casinoescape.movement.Direction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameContentActionsTest {
    @Test
    void pickUpAdjacentItemAddsItToInventoryAndClearsRoomCell() {
        Game game = Game.createNewGame(30);

        Item item = game.pickUpAdjacentItem();

        assertEquals(CasinoMapBuilder.BROKEN_BOTTLE_ID, item.getId());
        assertTrue(game.getInventory().containsItemId(CasinoMapBuilder.BROKEN_BOTTLE_ID));
        assertNull(game.getCurrentRoom().findItemAt(new Position(3, 4)));
        assertEquals(CellType.EMPTY, game.getCurrentRoom().getCell(new Position(3, 4)).getType());
        assertTrue(game.getTurnManager().hasActionBeenUsed());
    }

    @Test
    void attackAdjacentEnemyKillsItAndGrantsCrupierDrop() {
        Game game = Game.createNewGame(30);
        placePlayerInRoom(game, 4, new Position(3, 1));
        Enemy enemy = game.getCurrentRoom().findEnemyById(CasinoMapBuilder.BLACKJACK_DEALER_ENEMY_ID);
        enemy.setCurrentHealth(1);

        CombatResult result = game.attackEnemyAt(enemy.getPosition(), 1.0);

        assertTrue(result.isDefenderDied());
        assertEquals(6, result.getChipsAwarded());
        assertNull(game.getCurrentRoom().findEnemyById(CasinoMapBuilder.BLACKJACK_DEALER_ENEMY_ID));
        assertTrue(game.getInventory().containsItemId(CasinoMapBuilder.SHIELD_SUIT_ID));
        assertEquals(CellType.EMPTY, game.getCurrentRoom().getCell(new Position(3, 2)).getType());
    }

    @Test
    void lineMovementRequiresActivePillEffect() {
        Game game = Game.createNewGame(30);
        game.getInventory().restoreActiveEffect(new Effect(EffectType.LINE_MOVEMENT, 0, 7));

        game.movePlayerInLine(Direction.DOWN);

        assertEquals(new Position(6, 3), game.getPlayer().getPosition());
        assertTrue(game.getTurnManager().hasMovementBeenUsed());
    }

    private void placePlayerInRoom(Game game, int roomId, Position position) {
        Room currentRoom = game.getCurrentRoom();
        currentRoom.setCellType(game.getPlayer().getPosition(), CellType.EMPTY);
        game.getPlayer().setCurrentRoomId(roomId);
        game.getPlayer().setPosition(position);
        game.getCurrentRoom().setCellType(position, CellType.PLAYER);
    }
}
