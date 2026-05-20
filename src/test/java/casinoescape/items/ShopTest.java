package casinoescape.items;

import casinoescape.exceptions.NotEnoughChipsException;
import casinoescape.logging.GameLog;
import casinoescape.model.CasinoMap;
import casinoescape.model.Player;
import casinoescape.model.Position;
import casinoescape.game.CasinoMapBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShopTest {
    @Test
    void defaultBarShopContainsTreasuryKeyAndConsumable() {
        Shop shop = Shop.createDefaultBarShop();

        assertEquals(2, shop.size());
        assertEquals("Llave de Tesoreria", shop.findById(Shop.TREASURY_KEY_SHOP_ID).getName());
        assertEquals("Coctel curativo", shop.findById(Shop.HEALING_COCKTAIL_SHOP_ID).getName());
    }

    @Test
    void buyingItemSubtractsChipsAddsToInventoryAndLogsPurchase() {
        Shop shop = Shop.createDefaultBarShop();
        Player player = playerWithChips(Shop.HEALING_COCKTAIL_PRICE + 2);
        Inventory inventory = new Inventory();
        GameLog log = new GameLog();

        Item purchased = shop.buy(Shop.HEALING_COCKTAIL_SHOP_ID, player, inventory, log);

        assertEquals(2, player.getChips());
        assertEquals(1, inventory.size());
        assertSame(purchased, inventory.findById("HEALING_COCKTAIL"));
        assertEquals(1, log.size());
        assertTrue(log.getEntry(0).getMessage().contains("Coctel curativo"));
    }

    @Test
    void buyingWithoutEnoughChipsThrowsAndDoesNotChangeState() {
        Shop shop = Shop.createDefaultBarShop();
        Player player = playerWithChips(Shop.TREASURY_KEY_PRICE - 1);
        Inventory inventory = new Inventory();

        assertThrows(NotEnoughChipsException.class,
                () -> shop.buy(Shop.TREASURY_KEY_SHOP_ID, player, inventory));

        assertEquals(Shop.TREASURY_KEY_PRICE - 1, player.getChips());
        assertTrue(inventory.isEmpty());
        assertFalse(inventory.hasTreasuryKey());
    }

    @Test
    void buyingTreasuryKeyAddsKeyAndAllowsRoomThreeAccessCheck() {
        Shop shop = Shop.createDefaultBarShop();
        Player player = playerWithChips(Shop.TREASURY_KEY_PRICE);
        Inventory inventory = new Inventory();
        CasinoMap map = new CasinoMapBuilder().buildBaseMap();

        Item purchased = shop.buy(Shop.TREASURY_KEY_SHOP_ID, player, inventory);

        assertEquals(0, player.getChips());
        assertTrue(inventory.hasTreasuryKey());
        assertEquals(KeyItem.TREASURY_KEY_ID, purchased.getId());
        assertTrue(map.canTransition(2, 3, inventory.hasTreasuryKey()));
    }

    @Test
    void eachPurchaseCreatesSeparateItemInstance() {
        Shop shop = Shop.createDefaultBarShop();
        Player player = playerWithChips(Shop.HEALING_COCKTAIL_PRICE * 2);
        Inventory inventory = new Inventory();

        Item first = shop.buy(Shop.HEALING_COCKTAIL_SHOP_ID, player, inventory);
        Item second = shop.buy(Shop.HEALING_COCKTAIL_SHOP_ID, player, inventory);

        assertNotSame(first, second);
        assertEquals(2, inventory.size());
    }

    private Player playerWithChips(int chips) {
        Player player = new Player(100, 10, 5, 3, 1, new Position(3, 3));
        player.addChips(chips);
        return player;
    }
}
