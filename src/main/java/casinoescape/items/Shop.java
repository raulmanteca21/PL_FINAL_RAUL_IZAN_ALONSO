package casinoescape.items;

import casinoescape.exceptions.NotEnoughChipsException;
import casinoescape.logging.GameLog;
import casinoescape.model.Player;
import casinoescape.structures.MyLinkedList;

public class Shop {
    public static final String TREASURY_KEY_SHOP_ID = "SHOP_TREASURY_KEY";
    public static final String HEALING_COCKTAIL_SHOP_ID = "SHOP_HEALING_COCKTAIL";
    public static final int TREASURY_KEY_PRICE = 6;
    public static final int HEALING_COCKTAIL_PRICE = 3;

    private final MyLinkedList<ShopItem> items = new MyLinkedList<>();

    public static Shop createDefaultBarShop() {
        Shop shop = new Shop();
        shop.addItem(new ShopItem(
                TREASURY_KEY_SHOP_ID,
                "Llave de Tesoreria",
                TREASURY_KEY_PRICE,
                new KeyItem(KeyItem.TREASURY_KEY_ID, "Llave de Tesoreria")));
        shop.addItem(new ShopItem(
                HEALING_COCKTAIL_SHOP_ID,
                "Coctel curativo",
                HEALING_COCKTAIL_PRICE,
                new Consumable("HEALING_COCKTAIL", "Coctel curativo", new Effect(EffectType.HEAL, 25, 0))));
        return shop;
    }

    public void addItem(ShopItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Shop item is required");
        }
        items.add(item);
    }

    public int size() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public ShopItem getItem(int index) {
        return items.get(index);
    }

    public ShopItem findById(String id) {
        requireText(id, "Shop item id is required");
        for (int i = 0; i < items.size(); i++) {
            ShopItem item = items.get(i);
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }

    public Item buy(String shopItemId, Player player, Inventory inventory) {
        return buy(shopItemId, player, inventory, null);
    }

    public Item buy(String shopItemId, Player player, Inventory inventory, GameLog log) {
        requirePlayer(player);
        requireInventory(inventory);
        ShopItem shopItem = requireShopItem(shopItemId);
        if (player.getChips() < shopItem.getPrice()) {
            throw new NotEnoughChipsException("Not enough chips to buy " + shopItem.getName());
        }

        player.spendChips(shopItem.getPrice());
        Item purchasedItem = shopItem.createPurchasedItem();
        inventory.addItem(purchasedItem);
        if (log != null) {
            log.add("Compra en tienda: " + shopItem.getName() + " por " + shopItem.getPrice() + " fichas");
        }
        return purchasedItem;
    }

    private ShopItem requireShopItem(String id) {
        ShopItem item = findById(id);
        if (item == null) {
            throw new IllegalArgumentException("Shop item does not exist: " + id);
        }
        return item;
    }

    private void requirePlayer(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player is required");
        }
    }

    private void requireInventory(Inventory inventory) {
        if (inventory == null) {
            throw new IllegalArgumentException("Inventory is required");
        }
    }

    private void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}
