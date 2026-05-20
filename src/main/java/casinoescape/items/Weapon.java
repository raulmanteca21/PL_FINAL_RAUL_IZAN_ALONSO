package casinoescape.items;

public class Weapon extends Item {
    private final int attackBonus;

    public Weapon(String id, String name, int attackBonus) {
        super(id, name, ItemType.WEAPON);
        if (attackBonus < 0) {
            throw new IllegalArgumentException("Attack bonus cannot be negative");
        }
        this.attackBonus = attackBonus;
    }

    public int getAttackBonus() {
        return attackBonus;
    }
}
