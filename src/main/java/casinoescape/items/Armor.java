package casinoescape.items;

public class Armor extends Item {
    private final int defenseBonus;
    private final int attackBonus;

    public Armor(String id, String name, int defenseBonus) {
        this(id, name, defenseBonus, 0);
    }

    public Armor(String id, String name, int defenseBonus, int attackBonus) {
        super(id, name, ItemType.ARMOR);
        if (defenseBonus < 0 || attackBonus < 0) {
            throw new IllegalArgumentException("Armor bonuses cannot be negative");
        }
        this.defenseBonus = defenseBonus;
        this.attackBonus = attackBonus;
    }

    public int getDefenseBonus() {
        return defenseBonus;
    }

    public int getAttackBonus() {
        return attackBonus;
    }
}
