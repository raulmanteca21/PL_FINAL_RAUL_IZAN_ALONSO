package casinoescape.game;

import casinoescape.items.KeyItem;
import casinoescape.items.Shop;
import casinoescape.movement.ShortestPathInfo;
import casinoescape.model.CellType;
import casinoescape.model.GameState;
import casinoescape.model.Position;
import casinoescape.model.Room;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameTest {
    @Test
    void newGameStartsInRoomOne() {
        Game game = Game.createNewGame(30);

        assertEquals(1, game.getPlayer().getCurrentRoomId());
        assertEquals(new Position(3, 3), game.getPlayer().getPosition());
        assertEquals(CellType.PLAYER, game.getCurrentRoom().getCell(new Position(3, 3)).getType());
        assertEquals(GameState.IN_PROGRESS, game.getState());
    }

    @Test
    void gameMovesPlayerUsingMovementServiceAndLogsIt() {
        Game game = Game.createNewGame(30);
        Position destination = new Position(3, 4);

        game.movePlayer(destination);

        assertEquals(destination, game.getPlayer().getPosition());
        assertEquals(CellType.EMPTY, game.getMap().getRoom(1).getCell(new Position(3, 3)).getType());
        assertEquals(CellType.PLAYER, game.getMap().getRoom(1).getCell(destination).getType());
        assertTrue(game.getTurnManager().hasMovementBeenUsed());
        assertEquals(1, game.getLog().size());
    }

    @Test
    void buyingTreasuryKeyThroughGameAddsKeyAndSpendsChips() {
        Game game = Game.createNewGame(30);
        game.getPlayer().addChips(Shop.TREASURY_KEY_PRICE);

        game.buyFromBar(Shop.TREASURY_KEY_SHOP_ID);

        assertEquals(0, game.getPlayer().getChips());
        assertTrue(game.getInventory().hasTreasuryKey());
        assertTrue(game.getTurnManager().hasActionBeenUsed());
        assertEquals(1, game.getLog().size());
    }

    @Test
    void cannotUseLockedDoorWithoutTreasuryKey() {
        Game game = Game.createNewGame(30);
        placePlayerInRoom(game, 2, new Position(5, 3));

        assertFalse(game.canUseDoorTo(3));
        assertThrows(IllegalStateException.class, () -> game.useDoorTo(3));

        assertEquals(2, game.getPlayer().getCurrentRoomId());
        assertFalse(game.getTurnManager().hasActionBeenUsed());
        assertEquals(1, game.getLog().size());
    }

    @Test
    void canUseLockedDoorWithTreasuryKey() {
        Game game = Game.createNewGame(30);
        placePlayerInRoom(game, 2, new Position(5, 3));
        game.getInventory().addItem(new KeyItem(KeyItem.TREASURY_KEY_ID, CasinoMapBuilder.TREASURY_KEY_NAME));

        game.useDoorTo(3);

        assertEquals(3, game.getPlayer().getCurrentRoomId());
        assertEquals(new Position(5, 3), game.getPlayer().getPosition());
        assertEquals(CellType.PLAYER, game.getMap().getRoom(3).getCell(new Position(5, 3)).getType());
        assertEquals(29, game.getTurnManager().getTurnsRemaining());
        assertFalse(game.getTurnManager().hasActionBeenUsed());
        assertEquals(1, game.getLog().size());
    }

    @Test
    void shortestPathInfoIsExposedWithoutChangingGameState() {
        Game game = Game.createNewGame(30);
        int turnsBefore = game.getTurnManager().getTurnsRemaining();

        ShortestPathInfo info = game.getShortestPathInfo();

        assertEquals(1, info.getCurrentRoomId());
        assertEquals(4, info.getRoomDistance());
        assertEquals(new Position(3, 3), game.getPlayer().getPosition());
        assertEquals(turnsBefore, game.getTurnManager().getTurnsRemaining());
        assertFalse(game.getTurnManager().hasActionBeenUsed());
        assertFalse(game.getTurnManager().hasMovementBeenUsed());
        assertEquals(0, game.getLog().size());
    }

    @Test
    void shortestPathInfoUpdatesAfterRoomChangeAndMovement() {
        Game game = Game.createNewGame(30);
        placePlayerInRoom(game, 5, new Position(3, 3));

        ShortestPathInfo fromFive = game.getShortestPathInfo();
        placePlayerInRoom(game, 7, new Position(3, 5));
        ShortestPathInfo fromSeven = game.getShortestPathInfo();

        assertEquals(7, fromFive.getRecommendedNextRoomId());
        assertEquals(2, fromFive.getRoomDistance());
        assertEquals(8, fromSeven.getRecommendedNextRoomId());
        assertEquals(1, fromSeven.getRoomDistance());
        assertEquals(1, fromSeven.getCellDistance());
    }

    private void placePlayerInRoom(Game game, int roomId, Position position) {
        Room currentRoom = game.getCurrentRoom();
        currentRoom.setCellType(game.getPlayer().getPosition(), CellType.EMPTY);
        game.getPlayer().setCurrentRoomId(roomId);
        game.getPlayer().setPosition(position);
        game.getCurrentRoom().setCellType(position, CellType.PLAYER);
    }
}
