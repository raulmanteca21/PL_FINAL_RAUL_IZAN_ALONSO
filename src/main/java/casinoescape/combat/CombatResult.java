package casinoescape.combat;

public class CombatResult {
    private final int damageDealt;
    private final boolean defenderDied;
    private final int chipsAwarded;
    private final String droppedItemName;

    public CombatResult(int damageDealt, boolean defenderDied, int chipsAwarded, String droppedItemName) {
        if (damageDealt < 0 || chipsAwarded < 0) {
            throw new IllegalArgumentException("Combat result values cannot be negative");
        }
        this.damageDealt = damageDealt;
        this.defenderDied = defenderDied;
        this.chipsAwarded = chipsAwarded;
        this.droppedItemName = droppedItemName == null ? "" : droppedItemName;
    }

    public int getDamageDealt() {
        return damageDealt;
    }

    public boolean isDefenderDied() {
        return defenderDied;
    }

    public int getChipsAwarded() {
        return chipsAwarded;
    }

    public String getDroppedItemName() {
        return droppedItemName;
    }
}
