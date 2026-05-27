package casinoescape.game;

import casinoescape.items.KeyItem;
import casinoescape.items.Shop;
import casinoescape.model.CellType;
import casinoescape.model.Position;
import casinoescape.model.Room;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameUiSupportTest {
    @Test
    void exposesReachableCellsForRoomGridHighlighting() {
        Game game = Game.createNewGame(30);

        assertTrue(game.getReachableCells().contains(new Position(4, 3)));
        assertFalse(game.getReachableCells().contains(new Position(1, 1)));
    }

    @Test
    void useDoorAtRequiresAdjacentDoorPosition() {
        Game game = Game.createNewGame(30);

        assertThrows(IllegalStateException.class, () -> game.useDoorAt(new Position(0, 3)));

        assertEquals(1, game.getPlayer().getCurrentRoomId());
    }

    @Test
    void useDoorAtChangesRoomWhenAdjacentToValidDoor() {
        Game game = Game.createNewGame(30);
        placePlayerInRoom(game, 1, new Position(1, 3));

        game.useDoorAt(new Position(0, 3));

        assertEquals(2, game.getPlayer().getCurrentRoomId());
        assertEquals(29, game.getTurnManager().getTurnsRemaining());
        assertEquals(CellType.PLAYER, game.getCurrentRoom().getCell(game.getPlayer().getPosition()).getType());
    }

    @Test
    void useDoorAtRejectsPositionWithoutDoor() {
        Game game = Game.createNewGame(30);

        assertThrows(IllegalStateException.class, () -> game.useDoorAt(new Position(3, 4)));
    }

    @Test
    void buyFromAdjacentBarRequiresPlayerInBarRoom() {
        Game game = Game.createNewGame(30);
        game.getPlayer().addChips(Shop.TREASURY_KEY_PRICE);

        assertThrows(IllegalStateException.class, () -> game.buyFromAdjacentBar(Shop.TREASURY_KEY_SHOP_ID));

        assertFalse(game.getInventory().hasTreasuryKey());
    }

    @Test
    void buyFromAdjacentBarRequiresAdjacencyToShopCell() {
        Game game = Game.createNewGame(30);
        placePlayerInRoom(game, 5, new Position(0, 0));
        game.getPlayer().addChips(Shop.TREASURY_KEY_PRICE);

        assertThrows(IllegalStateException.class, () -> game.buyFromAdjacentBar(Shop.TREASURY_KEY_SHOP_ID));

        assertFalse(game.getInventory().hasTreasuryKey());
    }

    @Test
    void buyFromAdjacentBarBuysWhenAdjacentToShopCell() {
        Game game = Game.createNewGame(30);
        placePlayerInRoom(game, 5, new Position(3, 2));
        game.getPlayer().addChips(Shop.TREASURY_KEY_PRICE);

        game.buyFromAdjacentBar(Shop.TREASURY_KEY_SHOP_ID);

        assertTrue(game.getInventory().hasTreasuryKey());
        assertEquals(0, game.getPlayer().getChips());
        assertTrue(game.getTurnManager().hasActionBeenUsed());
    }

    @Test
    void useItemThroughGameConsumesActionAndLogs() {
        Game game = Game.createNewGame(30);
        game.getInventory().addItem(Shop.createDefaultBarShop()
                .findById(Shop.HEALING_COCKTAIL_SHOP_ID)
                .createPurchasedItem());
        game.getPlayer().setCurrentHealth(5);

        game.useItem("HEALING_COCKTAIL");

        assertEquals(Game.INITIAL_HEALTH, game.getPlayer().getCurrentHealth());
        assertTrue(game.getTurnManager().hasActionBeenUsed());
        assertEquals(1, game.getLog().size());
    }

    @Test
    void equipWeaponThroughGameConsumesActionAndLogs() {
        Game game = Game.createNewGame(30);
        game.getInventory().addItem(new casinoescape.items.Weapon("TEST_WEAPON", "Arma de prueba", 4));

        game.equipWeapon("TEST_WEAPON");

        assertEquals(Game.INITIAL_ATTACK + 4, game.getPlayer().getAttack());
        assertTrue(game.getTurnManager().hasActionBeenUsed());
        assertEquals(1, game.getLog().size());
    }

    @Test
    void lockedDoorCanBeOpenedFromUiSupportAfterBuyingKey() {
        Game game = Game.createNewGame(30);
        placePlayerInRoom(game, 2, new Position(1, 3));
        game.getInventory().addItem(new KeyItem(KeyItem.TREASURY_KEY_ID, CasinoMapBuilder.TREASURY_KEY_NAME));

        game.useDoorAt(new Position(0, 3));

        assertEquals(3, game.getPlayer().getCurrentRoomId());
    }

    private void placePlayerInRoom(Game game, int roomId, Position position) {
        Room currentRoom = game.getCurrentRoom();
        currentRoom.setCellType(game.getPlayer().getPosition(), CellType.EMPTY);
        game.getPlayer().setCurrentRoomId(roomId);
        game.getPlayer().setPosition(position);
        game.getCurrentRoom().setCellType(position, CellType.PLAYER);
    }
}
