package casinoescape.combat;

public class DamageCalculator {
    public int calculateDamage(int attack, int defense, double randomValue) {
        if (attack < 0 || defense < 0) {
            throw new IllegalArgumentException("Attack and defense cannot be negative");
        }
        if (randomValue < 0.0 || randomValue > 1.0) {
            throw new IllegalArgumentException("Random value must be between 0 and 1");
        }
        double rawDamage = attack * (randomValue * 2.0) - defense;
        return Math.max(0, (int) rawDamage);
    }
}
