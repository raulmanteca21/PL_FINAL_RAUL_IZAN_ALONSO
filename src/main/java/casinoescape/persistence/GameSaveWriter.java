package casinoescape.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import casinoescape.exceptions.PersistenceException;
import casinoescape.game.Game;
import casinoescape.items.Armor;
import casinoescape.items.Consumable;
import casinoescape.items.Effect;
import casinoescape.items.Inventory;
import casinoescape.items.Item;
import casinoescape.items.Weapon;
import casinoescape.logging.GameLog;
import casinoescape.logging.LogEntry;
import casinoescape.model.Player;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GameSaveWriter {
    private final Gson gson;

    public GameSaveWriter() {
        this(new GsonBuilder().setPrettyPrinting().create());
    }

    public GameSaveWriter(Gson gson) {
        if (gson == null) {
            throw new IllegalArgumentException("Gson is required");
        }
        this.gson = gson;
    }

    public void save(Game game, Path path) {
        if (game == null) {
            throw new IllegalArgumentException("Game is required");
        }
        if (path == null) {
            throw new IllegalArgumentException("Save path is required");
        }
        PersistenceData.SaveGameData data = createSaveData(game);
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(path, gson.toJson(data));
        } catch (IOException exception) {
            throw new PersistenceException("Could not write save file", exception);
        }
    }

    private PersistenceData.SaveGameData createSaveData(Game game) {
        PersistenceData.SaveGameData data = new PersistenceData.SaveGameData();
        data.version = 1;
        data.state = game.getState().name();
        data.player = createPlayerData(game.getPlayer());
        data.turn = createTurnData(game);
        data.inventory = createInventoryData(game.getInventory());
        data.interactives = createInteractivesData(game);
        data.enemies = new PersistenceData.EnemySaveData[0];
        data.collectedObjectIds = new String[0];
        data.doors = createDoorData(game);
        data.treasuryKeyBought = game.getInventory().hasTreasuryKey();
        data.log = createLogData(game.getLog());
        return data;
    }

    private PersistenceData.PlayerData createPlayerData(Player player) {
        PersistenceData.PlayerData data = new PersistenceData.PlayerData();
        data.currentRoomId = player.getCurrentRoomId();
        data.row = player.getPosition().getRow();
        data.column = player.getPosition().getColumn();
        data.maxHealth = player.getMaxHealth();
        data.currentHealth = player.getCurrentHealth();
        data.attack = player.getAttack();
        data.defense = player.getDefense();
        data.movementPoints = player.getMovementPoints();
        data.chips = player.getChips();
        data.friendRescued = player.isFriendRescued();
        return data;
    }

    private PersistenceData.TurnData createTurnData(Game game) {
        PersistenceData.TurnData data = new PersistenceData.TurnData();
        data.turnsRemaining = game.getTurnManager().getTurnsRemaining();
        data.movementUsed = game.getTurnManager().hasMovementBeenUsed();
        data.actionUsed = game.getTurnManager().hasActionBeenUsed();
        data.enemyPhaseProcessedLastTurn = game.getTurnManager().wasEnemyPhaseProcessedLastTurn();
        data.phase = game.getTurnManager().getPhase().name();
        return data;
    }

    private PersistenceData.InventoryData createInventoryData(Inventory inventory) {
        PersistenceData.InventoryData data = new PersistenceData.InventoryData();
        data.items = new PersistenceData.ItemData[inventory.size()];
        for (int i = 0; i < inventory.size(); i++) {
            data.items[i] = createItemData(inventory.getItem(i));
        }
        data.equippedWeaponId = inventory.getEquippedWeapon() == null ? "" : inventory.getEquippedWeapon().getId();
        data.equippedArmorId = inventory.getEquippedArmor() == null ? "" : inventory.getEquippedArmor().getId();
        data.activeEffects = new PersistenceData.EffectData[inventory.activeEffectCount()];
        for (int i = 0; i < inventory.activeEffectCount(); i++) {
            data.activeEffects[i] = createEffectData(inventory.getActiveEffect(i));
        }
        return data;
    }

    private PersistenceData.ItemData createItemData(Item item) {
        PersistenceData.ItemData data = new PersistenceData.ItemData();
        data.id = item.getId();
        data.name = item.getName();
        data.type = item.getType().name();
        if (item instanceof Weapon) {
            data.attackBonus = ((Weapon) item).getAttackBonus();
        } else if (item instanceof Armor) {
            Armor armor = (Armor) item;
            data.defenseBonus = armor.getDefenseBonus();
            data.attackBonus = armor.getAttackBonus();
        } else if (item instanceof Consumable) {
            data.effect = createEffectData(((Consumable) item).getEffect());
        }
        return data;
    }

    private PersistenceData.EffectData createEffectData(Effect effect) {
        PersistenceData.EffectData data = new PersistenceData.EffectData();
        data.type = effect.getType().name();
        data.amount = effect.getAmount();
        data.remainingTurns = effect.getRemainingTurns();
        return data;
    }

    private PersistenceData.InteractivesData createInteractivesData(Game game) {
        PersistenceData.InteractivesData data = new PersistenceData.InteractivesData();
        data.welcomeNpcInteracted = game.getWelcomeNpc().hasAlreadyInteracted();
        data.barSpecialNpcInteracted = game.getBarSpecialNpc().hasAlreadyInteracted();
        data.friendNpcInteracted = game.getFriendNpc().hasAlreadyInteracted();
        return data;
    }

    private PersistenceData.LogEntryData[] createLogData(GameLog log) {
        PersistenceData.LogEntryData[] data = new PersistenceData.LogEntryData[log.size()];
        for (int i = 0; i < log.size(); i++) {
            LogEntry entry = log.getEntry(i);
            data[i] = new PersistenceData.LogEntryData();
            data[i].turn = entry.getTurn();
            data[i].message = entry.getMessage();
        }
        return data;
    }

    private PersistenceData.DoorSaveData[] createDoorData(Game game) {
        PersistenceData.DoorSaveData[] data = new PersistenceData.DoorSaveData[1];
        data[0] = new PersistenceData.DoorSaveData();
        data[0].fromRoomId = 2;
        data[0].toRoomId = 3;
        data[0].locked = !game.getInventory().hasTreasuryKey();
        return data;
    }
}
