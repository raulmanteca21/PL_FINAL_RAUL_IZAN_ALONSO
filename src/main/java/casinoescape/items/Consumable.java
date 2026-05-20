package casinoescape.items;

public class Consumable extends Item {
    private final Effect effect;

    public Consumable(String id, String name, Effect effect) {
        super(id, name, ItemType.CONSUMABLE);
        if (effect == null) {
            throw new IllegalArgumentException("Consumable effect is required");
        }
        this.effect = effect;
    }

    public Effect getEffect() {
        return effect;
    }
}
