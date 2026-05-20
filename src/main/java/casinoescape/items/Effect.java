package casinoescape.items;

public class Effect {
    private final EffectType type;
    private final int amount;
    private int remainingTurns;

    public Effect(EffectType type, int amount, int remainingTurns) {
        if (type == null) {
            throw new IllegalArgumentException("Effect type is required");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Effect amount cannot be negative");
        }
        if (remainingTurns < 0) {
            throw new IllegalArgumentException("Effect duration cannot be negative");
        }
        this.type = type;
        this.amount = amount;
        this.remainingTurns = remainingTurns;
    }

    public EffectType getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    public int getRemainingTurns() {
        return remainingTurns;
    }

    public void decreaseTurn() {
        if (remainingTurns > 0) {
            remainingTurns--;
        }
    }

    public boolean isExpired() {
        return remainingTurns == 0;
    }
}
