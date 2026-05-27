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
    void defaultBarShopContainsRequiredItems() {
        Shop shop = Shop.createDefaultBarShop();

        assertEquals(4, shop.size());
        assertEquals("Llave de Tesoreria", shop.findById(Shop.TREASURY_KEY_SHOP_ID).getName());
        assertEquals("Vodka Redbull", shop.findById(Shop.VODKA_REDBULL_SHOP_ID).getName());
        assertEquals("Coctel curativo", shop.findById(Shop.HEALING_COCKTAIL_SHOP_ID).getName());
        assertEquals("Chaleco de portero", shop.findById(Shop.BOUNCER_VEST_SHOP_ID).getName());
        assertEquals(Shop.TREASURY_KEY_PRICE, shop.findById(Shop.TREASURY_KEY_SHOP_ID).getPrice());
        assertEquals(Shop.VODKA_REDBULL_PRICE, shop.findById(Shop.VODKA_REDBULL_SHOP_ID).getPrice());
        assertEquals(Shop.HEALING_COCKTAIL_PRICE, shop.findById(Shop.HEALING_COCKTAIL_SHOP_ID).getPrice());
        assertEquals(Shop.BOUNCER_VEST_PRICE, shop.findById(Shop.BOUNCER_VEST_SHOP_ID).getPrice());
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
    void buyingWithFullInventoryDoesNotSpendChips() {
        Shop shop = Shop.createDefaultBarShop();
        Player player = playerWithChips(Shop.HEALING_COCKTAIL_PRICE);
        Inventory inventory = new Inventory();
        for (int i = 0; i < Inventory.MAX_ITEMS; i++) {
            inventory.addItem(new Weapon("WEAPON_" + i, "Arma " + i, 1));
        }

        assertThrows(IllegalStateException.class,
                () -> shop.buy(Shop.HEALING_COCKTAIL_SHOP_ID, player, inventory));

        assertEquals(Shop.HEALING_COCKTAIL_PRICE, player.getChips());
        assertEquals(Inventory.MAX_ITEMS, inventory.size());
    }

    @Test
    void defaultBarShopAllowsBuyingEveryRequiredItem() {
        Shop shop = Shop.createDefaultBarShop();
        Player player = playerWithChips(Shop.TREASURY_KEY_PRICE + Shop.VODKA_REDBULL_PRICE
                + Shop.HEALING_COCKTAIL_PRICE + Shop.BOUNCER_VEST_PRICE);
        Inventory inventory = new Inventory();

        shop.buy(Shop.TREASURY_KEY_SHOP_ID, player, inventory);
        shop.buy(Shop.VODKA_REDBULL_SHOP_ID, player, inventory);
        shop.buy(Shop.HEALING_COCKTAIL_SHOP_ID, player, inventory);
        shop.buy(Shop.BOUNCER_VEST_SHOP_ID, player, inventory);

        assertTrue(inventory.containsItemId(KeyItem.TREASURY_KEY_ID));
        assertTrue(inventory.containsItemId("VODKA_REDBULL"));
        assertTrue(inventory.containsItemId("HEALING_COCKTAIL"));
        assertTrue(inventory.containsItemId("BOUNCER_VEST"));
        assertEquals(0, player.getChips());
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
