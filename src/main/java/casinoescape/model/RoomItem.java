package casinoescape.model;

import casinoescape.items.Item;

public class RoomItem {
    private final Item item;
    private final Position position;

    public RoomItem(Item item, Position position) {
        if (item == null) {
            throw new IllegalArgumentException("Item is required");
        }
        if (position == null) {
            throw new IllegalArgumentException("Position is required");
        }
        this.item = item;
        this.position = position;
    }

    public Item getItem() {
        return item;
    }

    public Position getPosition() {
        return position;
    }
}
