package casinoescape.combat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DamageCalculatorTest {
    private final DamageCalculator damageCalculator = new DamageCalculator();

    @Test
    void calculateDamageAppliesOfficialFormula() {
        int damage = damageCalculator.calculateDamage(10, 5, 0.75);

        assertEquals(10, damage);
    }

    @Test
    void calculateDamageNeverReturnsNegativeDamage() {
        int damage = damageCalculator.calculateDamage(4, 20, 0.5);

        assertEquals(0, damage);
    }

    @Test
    void calculateDamageTruncatesDecimalResult() {
        int damage = damageCalculator.calculateDamage(5, 0, 0.75);

        assertEquals(7, damage);
    }

    @Test
    void invalidArgumentsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> damageCalculator.calculateDamage(-1, 0, 0.5));
        assertThrows(IllegalArgumentException.class, () -> damageCalculator.calculateDamage(1, -1, 0.5));
        assertThrows(IllegalArgumentException.class, () -> damageCalculator.calculateDamage(1, 0, -0.1));
        assertThrows(IllegalArgumentException.class, () -> damageCalculator.calculateDamage(1, 0, 1.1));
    }
}
