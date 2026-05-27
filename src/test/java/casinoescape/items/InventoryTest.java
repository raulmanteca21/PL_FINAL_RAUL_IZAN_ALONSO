package casinoescape.items;

import casinoescape.model.Player;
import casinoescape.model.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InventoryTest {
    @Test
    void newInventoryIsEmpty() {
        Inventory inventory = new Inventory();

        assertTrue(inventory.isEmpty());
        assertEquals(0, inventory.size());
        assertNull(inventory.getEquippedWeapon());
        assertNull(inventory.getEquippedArmor());
    }

    @Test
    void itemsCanBeAddedFoundAndRemoved() {
        Inventory inventory = new Inventory();
        Weapon weapon = new Weapon("BROKEN_BOTTLE", "Botella rota", 2);

        inventory.addItem(weapon);

        assertFalse(inventory.isEmpty());
        assertEquals(1, inventory.size());
        assertSame(weapon, inventory.getItem(0));
        assertSame(weapon, inventory.findById("BROKEN_BOTTLE"));
        assertSame(weapon, inventory.findByName("Botella rota"));
        assertTrue(inventory.containsItemId("BROKEN_BOTTLE"));

        assertTrue(inventory.removeItem(weapon));
        assertTrue(inventory.isEmpty());
    }

    @Test
    void inventoryRejectsItemsOverMaximumCapacity() {
        Inventory inventory = new Inventory();
        for (int i = 0; i < Inventory.MAX_ITEMS; i++) {
            inventory.addItem(new Weapon("WEAPON_" + i, "Arma " + i, 1));
        }

        assertThrows(IllegalStateException.class,
                () -> inventory.addItem(new Weapon("EXTRA", "Extra", 1)));
        assertEquals(Inventory.MAX_ITEMS, inventory.size());
    }

    @Test
    void firstWeaponAddedWithPlayerIsEquippedAutomatically() {
        Inventory inventory = new Inventory();
        Player player = player();
        Weapon weapon = new Weapon("BROKEN_BOTTLE", "Botella rota", 3);

        inventory.addItem(weapon, player);

        assertSame(weapon, inventory.getEquippedWeapon());
        assertEquals(13, player.getAttack());
    }

    @Test
    void addingSecondWeaponDoesNotReplaceEquippedWeaponAutomatically() {
        Inventory inventory = new Inventory();
        Player player = player();
        Weapon first = new Weapon("BROKEN_BOTTLE", "Botella rota", 3);
        Weapon second = new Weapon("CANE", "Baston", 6);

        inventory.addItem(first, player);
        inventory.addItem(second, player);

        assertSame(first, inventory.getEquippedWeapon());
        assertEquals(13, player.getAttack());
    }

    @Test
    void removingEquippedWeaponRevertsAttackBonus() {
        Inventory inventory = new Inventory();
        Player player = player();
        Weapon weapon = new Weapon("BROKEN_BOTTLE", "Botella rota", 3);
        inventory.addItem(weapon, player);

        assertTrue(inventory.removeItem(weapon, player));

        assertNull(inventory.getEquippedWeapon());
        assertEquals(10, player.getAttack());
    }

    @Test
    void removingEquippedArmorRevertsDefenseBonus() {
        Inventory inventory = new Inventory();
        Player player = player();
        Armor armor = new Armor("SHIELD_SUIT", "Traje con escudo", 3);
        inventory.addItem(armor);
        inventory.equipArmor("SHIELD_SUIT", player);

        Item removed = inventory.removeAt(0, player);

        assertSame(armor, removed);
        assertNull(inventory.getEquippedArmor());
        assertEquals(5, player.getDefense());
    }

    @Test
    void consumableHealsWithoutExceedingMaxHealthAndIsRemoved() {
        Inventory inventory = new Inventory();
        Player player = player();
        player.setCurrentHealth(85);
        Consumable drink = new Consumable("ENERGY_DRINK", "Bebida energetica", new Effect(EffectType.HEAL, 25, 0));
        inventory.addItem(drink);

        inventory.useConsumable("ENERGY_DRINK", player);

        assertEquals(100, player.getCurrentHealth());
        assertFalse(inventory.containsItemId("ENERGY_DRINK"));
        assertEquals(0, inventory.size());
    }

    @Test
    void equippingWeaponAppliesAttackBonus() {
        Inventory inventory = new Inventory();
        Player player = player();
        Weapon weapon = new Weapon("CANE", "Baston de jefe de sala", 4);
        inventory.addItem(weapon);

        inventory.equipWeapon("CANE", player);

        assertSame(weapon, inventory.getEquippedWeapon());
        assertEquals(14, player.getAttack());
    }

    @Test
    void equippingSecondWeaponReplacesPreviousBonus() {
        Inventory inventory = new Inventory();
        Player player = player();
        inventory.addItem(new Weapon("CANE", "Baston de jefe de sala", 4));
        Weapon betterWeapon = new Weapon("BRASS_KNUCKLES", "Puno americano", 7);
        inventory.addItem(betterWeapon);

        inventory.equipWeapon("CANE", player);
        inventory.equipWeapon("BRASS_KNUCKLES", player);

        assertSame(betterWeapon, inventory.getEquippedWeapon());
        assertEquals(17, player.getAttack());
    }

    @Test
    void equippingArmorAppliesDefenseBonus() {
        Inventory inventory = new Inventory();
        Player player = player();
        Armor armor = new Armor("SHIELD_SUIT", "Traje con escudo", 3);
        inventory.addItem(armor);

        inventory.equipArmor("SHIELD_SUIT", player);

        assertSame(armor, inventory.getEquippedArmor());
        assertEquals(8, player.getDefense());
    }

    @Test
    void equippingSecondArmorReplacesPreviousBonus() {
        Inventory inventory = new Inventory();
        Player player = player();
        inventory.addItem(new Armor("SHIELD_SUIT", "Traje con escudo", 3));
        Armor betterArmor = new Armor("SECURITY_VEST", "Chaleco de seguridad", 5);
        inventory.addItem(betterArmor);

        inventory.equipArmor("SHIELD_SUIT", player);
        inventory.equipArmor("SECURITY_VEST", player);

        assertSame(betterArmor, inventory.getEquippedArmor());
        assertEquals(10, player.getDefense());
    }

    @Test
    void treasuryKeyIsDetectedAndNotConsumed() {
        Inventory inventory = new Inventory();
        KeyItem key = new KeyItem(KeyItem.TREASURY_KEY_ID, "Llave de Tesoreria");
        inventory.addItem(key);

        assertTrue(inventory.hasTreasuryKey());
        assertSame(key, inventory.findById(KeyItem.TREASURY_KEY_ID));
        assertEquals(1, inventory.size());
    }

    @Test
    void tobaccoAppliesTemporaryMovementBonus() {
        Inventory inventory = new Inventory();
        Player player = player();
        inventory.addItem(new Consumable("TOBACCO_PACK", "Cajetilla de tabaco", new Effect(EffectType.MOVEMENT_BONUS, 1, 2)));

        inventory.useConsumable("TOBACCO_PACK", player);

        assertEquals(4, player.getMovementPoints());
        assertTrue(inventory.hasActiveEffect(EffectType.MOVEMENT_BONUS));
        assertEquals(2, inventory.getActiveEffectTurns(EffectType.MOVEMENT_BONUS));
        inventory.decreaseTemporaryEffects(player);
        assertEquals(1, inventory.getActiveEffectTurns(EffectType.MOVEMENT_BONUS));
        inventory.decreaseTemporaryEffects(player);
        assertEquals(3, player.getMovementPoints());
        assertFalse(inventory.hasActiveEffect(EffectType.MOVEMENT_BONUS));
    }

    @Test
    void suspiciousPillActivatesLineMovementForSevenTurns() {
        Inventory inventory = new Inventory();
        Player player = player();
        inventory.addItem(new Consumable("SUSPICIOUS_PILL", "Pastilla de dudosa procedencia", new Effect(EffectType.LINE_MOVEMENT, 0, 7)));

        inventory.useConsumable("SUSPICIOUS_PILL", player);

        assertEquals(10, player.getAttack());
        assertTrue(inventory.hasActiveEffect(EffectType.LINE_MOVEMENT));
        assertEquals(7, inventory.getActiveEffectTurns(EffectType.LINE_MOVEMENT));
        assertFalse(inventory.containsItemId("SUSPICIOUS_PILL"));

        for (int i = 0; i < 7; i++) {
            inventory.decreaseTemporaryEffects(player);
        }

        assertFalse(inventory.hasActiveEffect(EffectType.LINE_MOVEMENT));
        assertEquals(10, player.getAttack());
    }

    @Test
    void missingOrWrongTypeItemsAreRejected() {
        Inventory inventory = new Inventory();
        Player player = player();
        inventory.addItem(new Weapon("CANE", "Baston de jefe de sala", 4));

        assertThrows(IllegalArgumentException.class, () -> inventory.useConsumable("MISSING", player));
        assertThrows(IllegalArgumentException.class, () -> inventory.useConsumable("CANE", player));
        assertThrows(IllegalArgumentException.class, () -> inventory.equipArmor("CANE", player));
    }

    private Player player() {
        return new Player(100, 10, 5, 3, 1, new Position(3, 3));
    }
}
