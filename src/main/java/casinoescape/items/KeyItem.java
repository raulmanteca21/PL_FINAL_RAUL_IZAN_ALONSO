package casinoescape.items;

public class KeyItem extends Item {
    public static final String TREASURY_KEY_ID = "TREASURY_KEY";

    public KeyItem(String id, String name) {
        super(id, name, ItemType.KEY);
    }
}
