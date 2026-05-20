package casinoescape.items;

import casinoescape.model.Player;
import casinoescape.structures.MyLinkedList;

public class Inventory {
    private final MyLinkedList<Item> items = new MyLinkedList<>();
    private final MyLinkedList<Effect> activeEffects = new MyLinkedList<>();
    private Weapon equippedWeapon;
    private Armor equippedArmor;

    public void addItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item is required");
        }
        items.add(item);
    }

    public boolean removeItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item is required");
        }
        return items.remove(item);
    }

    public Item removeAt(int index) {
        return items.removeAt(index);
    }

    public Item getItem(int index) {
        return items.get(index);
    }

    public int size() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void clear() {
        items.clear();
        activeEffects.clear();
        equippedWeapon = null;
        equippedArmor = null;
    }

    public boolean contains(Item item) {
        if (item == null) {
            return false;
        }
        return items.contains(item);
    }

    public boolean containsItemId(String id) {
        return findById(id) != null;
    }

    public Item findById(String id) {
        requireText(id, "Item id is required");
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }

    public Item findByName(String name) {
        requireText(name, "Item name is required");
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }

    public boolean hasTreasuryKey() {
        return containsItemId(KeyItem.TREASURY_KEY_ID);
    }

    public void equipWeapon(String itemId, Player player) {
        requirePlayer(player);
        Item item = requireExistingItem(itemId);
        if (!(item instanceof Weapon)) {
            throw new IllegalArgumentException("Item is not a weapon");
        }
        if (equippedWeapon != null) {
            player.setAttack(player.getAttack() - equippedWeapon.getAttackBonus());
        }
        equippedWeapon = (Weapon) item;
        player.setAttack(player.getAttack() + equippedWeapon.getAttackBonus());
    }

    public void equipArmor(String itemId, Player player) {
        requirePlayer(player);
        Item item = requireExistingItem(itemId);
        if (!(item instanceof Armor)) {
            throw new IllegalArgumentException("Item is not armor");
        }
        if (equippedArmor != null) {
            player.setDefense(player.getDefense() - equippedArmor.getDefenseBonus());
            player.setAttack(player.getAttack() - equippedArmor.getAttackBonus());
        }
        equippedArmor = (Armor) item;
        player.setDefense(player.getDefense() + equippedArmor.getDefenseBonus());
        player.setAttack(player.getAttack() + equippedArmor.getAttackBonus());
    }

    public void useConsumable(String itemId, Player player) {
        requirePlayer(player);
        Item item = requireExistingItem(itemId);
        if (!(item instanceof Consumable)) {
            throw new IllegalArgumentException("Item is not a consumable");
        }
        Consumable consumable = (Consumable) item;
        applyEffect(consumable.getEffect(), player);
        items.remove(consumable);
    }

    public void decreaseTemporaryEffects(Player player) {
        requirePlayer(player);
        int index = 0;
        while (index < activeEffects.size()) {
            Effect effect = activeEffects.get(index);
            effect.decreaseTurn();
            if (effect.isExpired()) {
                revertEffect(effect, player);
                activeEffects.removeAt(index);
            } else {
                index++;
            }
        }
    }

    public boolean hasActiveEffect(EffectType type) {
        if (type == null) {
            return false;
        }
        for (int i = 0; i < activeEffects.size(); i++) {
            if (activeEffects.get(i).getType() == type) {
                return true;
            }
        }
        return false;
    }

    public int getActiveEffectTurns(EffectType type) {
        if (type == null) {
            return 0;
        }
        for (int i = 0; i < activeEffects.size(); i++) {
            Effect effect = activeEffects.get(i);
            if (effect.getType() == type) {
                return effect.getRemainingTurns();
            }
        }
        return 0;
    }

    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    public Armor getEquippedArmor() {
        return equippedArmor;
    }

    private void applyEffect(Effect effect, Player player) {
        if (effect.getType() == EffectType.HEAL) {
            player.setCurrentHealth(player.getCurrentHealth() + effect.getAmount());
            return;
        }
        Effect activeEffect = new Effect(effect.getType(), effect.getAmount(), effect.getRemainingTurns());
        if (activeEffect.getType() == EffectType.MOVEMENT_BONUS) {
            player.setMovementPoints(player.getMovementPoints() + activeEffect.getAmount());
        } else if (activeEffect.getType() == EffectType.ATTACK_BONUS) {
            player.setAttack(player.getAttack() + activeEffect.getAmount());
        } else if (activeEffect.getType() == EffectType.DEFENSE_BONUS) {
            player.setDefense(player.getDefense() + activeEffect.getAmount());
        }
        activeEffects.add(activeEffect);
    }

    private void revertEffect(Effect effect, Player player) {
        if (effect.getType() == EffectType.MOVEMENT_BONUS) {
            player.setMovementPoints(player.getMovementPoints() - effect.getAmount());
        } else if (effect.getType() == EffectType.ATTACK_BONUS) {
            player.setAttack(player.getAttack() - effect.getAmount());
        } else if (effect.getType() == EffectType.DEFENSE_BONUS) {
            player.setDefense(player.getDefense() - effect.getAmount());
        }
    }

    private Item requireExistingItem(String itemId) {
        Item item = findById(itemId);
        if (item == null) {
            throw new IllegalArgumentException("Item is not in inventory: " + itemId);
        }
        return item;
    }

    private void requirePlayer(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player is required");
        }
    }

    private void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}
