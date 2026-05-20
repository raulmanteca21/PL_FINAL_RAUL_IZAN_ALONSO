package casinoescape.items;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ItemTest {
    @Test
    void itemStoresIdentityNameAndType() {
        Weapon weapon = new Weapon("CANE", "Baston de jefe de sala", 4);

        assertEquals("CANE", weapon.getId());
        assertEquals("Baston de jefe de sala", weapon.getName());
        assertEquals(ItemType.WEAPON, weapon.getType());
        assertEquals(4, weapon.getAttackBonus());
    }

    @Test
    void equalityUsesStableId() {
        Weapon weapon = new Weapon("SHARED_ID", "Nombre A", 1);
        Armor armor = new Armor("SHARED_ID", "Nombre B", 2);
        KeyItem key = new KeyItem("OTHER_ID", "Llave");

        assertEquals(weapon, armor);
        assertEquals(weapon.hashCode(), armor.hashCode());
        assertNotEquals(weapon, key);
    }

    @Test
    void invalidItemDataIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Weapon("", "Baston", 1));
        assertThrows(IllegalArgumentException.class, () -> new Weapon("CANE", "", 1));
        assertThrows(IllegalArgumentException.class, () -> new Weapon("CANE", "Baston", -1));
        assertThrows(IllegalArgumentException.class, () -> new Armor("ARMOR", "Armadura", -1));
        assertThrows(IllegalArgumentException.class, () -> new Consumable("DRINK", "Bebida", null));
        assertThrows(IllegalArgumentException.class, () -> new Effect(null, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> new Effect(EffectType.HEAL, -1, 0));
        assertThrows(IllegalArgumentException.class, () -> new Effect(EffectType.HEAL, 1, -1));
    }
}
