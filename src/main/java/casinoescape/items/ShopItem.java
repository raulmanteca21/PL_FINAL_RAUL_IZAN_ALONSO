package casinoescape.items;

public class ShopItem {
    private final String id;
    private final String name;
    private final int price;
    private final Item item;

    public ShopItem(String id, String name, int price, Item item) {
        requireText(id, "Shop item id is required");
        requireText(name, "Shop item name is required");
        if (price < 0) {
            throw new IllegalArgumentException("Shop item price cannot be negative");
        }
        if (item == null) {
            throw new IllegalArgumentException("Shop item requires an item");
        }
        this.id = id;
        this.name = name;
        this.price = price;
        this.item = item;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public Item createPurchasedItem() {
        if (item instanceof KeyItem) {
            return new KeyItem(item.getId(), item.getName());
        }
        if (item instanceof Consumable) {
            Consumable consumable = (Consumable) item;
            Effect effect = consumable.getEffect();
            return new Consumable(item.getId(), item.getName(), new Effect(effect.getType(), effect.getAmount(), effect.getRemainingTurns()));
        }
        if (item instanceof Armor) {
            Armor armor = (Armor) item;
            return new Armor(item.getId(), item.getName(), armor.getDefenseBonus(), armor.getAttackBonus());
        }
        if (item instanceof Weapon) {
            Weapon weapon = (Weapon) item;
            return new Weapon(item.getId(), item.getName(), weapon.getAttackBonus());
        }
        throw new IllegalStateException("Unsupported shop item type");
    }

    private void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}
