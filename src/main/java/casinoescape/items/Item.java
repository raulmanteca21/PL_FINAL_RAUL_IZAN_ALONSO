package casinoescape.items;

public abstract class Item {
    private final String id;
    private final String name;
    private final ItemType type;

    protected Item(String id, String name, ItemType type) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Item id is required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Item name is required");
        }
        if (type == null) {
            throw new IllegalArgumentException("Item type is required");
        }
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ItemType getType() {
        return type;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Item)) {
            return false;
        }
        Item item = (Item) other;
        return id.equals(item.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
