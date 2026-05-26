package casinoescape.persistence;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import casinoescape.exceptions.InvalidConfigurationException;
import casinoescape.exceptions.PersistenceException;
import casinoescape.game.CasinoMapBuilder;
import casinoescape.game.Game;
import casinoescape.game.TurnManager;
import casinoescape.game.TurnPhase;
import casinoescape.items.Armor;
import casinoescape.items.Consumable;
import casinoescape.items.Effect;
import casinoescape.items.EffectType;
import casinoescape.items.Inventory;
import casinoescape.items.Item;
import casinoescape.items.ItemType;
import casinoescape.items.KeyItem;
import casinoescape.items.Shop;
import casinoescape.items.Weapon;
import casinoescape.logging.GameLog;
import casinoescape.model.CasinoMap;
import casinoescape.model.CellType;
import casinoescape.model.Enemy;
import casinoescape.model.GameState;
import casinoescape.model.Player;
import casinoescape.model.Position;
import casinoescape.model.Room;
import casinoescape.movement.MovementService;
import casinoescape.movement.PathFinder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GameSaveLoader {
    private final Gson gson;
    private final JsonValidator validator;

    public GameSaveLoader() {
        this(new Gson(), new JsonValidator());
    }

    public GameSaveLoader(Gson gson, JsonValidator validator) {
        if (gson == null) {
            throw new IllegalArgumentException("Gson is required");
        }
        if (validator == null) {
            throw new IllegalArgumentException("Json validator is required");
        }
        this.gson = gson;
        this.validator = validator;
    }

    public Game load(Path path) {
        PersistenceData.SaveGameData save = readSave(path);
        CasinoMap map = new CasinoMapBuilder().buildBaseMap();
        validator.validateSave(save, map);
        if (save.treasuryKeyBought && !containsTreasuryKey(save.inventory)) {
            throw new InvalidConfigurationException("Treasury key state is inconsistent");
        }
        return createGame(save, map);
    }

    private boolean containsTreasuryKey(PersistenceData.InventoryData inventory) {
        if (inventory == null || inventory.items == null) {
            return false;
        }
        for (int i = 0; i < inventory.items.length; i++) {
            if (inventory.items[i] != null && KeyItem.TREASURY_KEY_ID.equals(inventory.items[i].id)) {
                return true;
            }
        }
        return false;
    }

    private PersistenceData.SaveGameData readSave(Path path) {
        if (path == null) {
            throw new IllegalArgumentException("Save path is required");
        }
        if (!Files.exists(path)) {
            throw new PersistenceException("Save file does not exist: " + path);
        }
        try {
            return gson.fromJson(Files.readString(path), PersistenceData.SaveGameData.class);
        } catch (JsonSyntaxException exception) {
            throw new PersistenceException("Save JSON is malformed", exception);
        } catch (IOException exception) {
            throw new PersistenceException("Could not read save file", exception);
        }
    }

    private Game createGame(PersistenceData.SaveGameData save, CasinoMap map) {
        Player player = createPlayer(save.player);
        Inventory inventory = createInventory(save.inventory);
        TurnManager turnManager = TurnManager.restored(
                save.turn.turnsRemaining,
                save.turn.movementUsed,
                save.turn.actionUsed,
                save.turn.enemyPhaseProcessedLastTurn,
                TurnPhase.valueOf(save.turn.phase),
                GameState.valueOf(save.state));
        GameLog log = createLog(save.log);
        Game game = new Game(
                map,
                player,
                inventory,
                turnManager,
                new MovementService(),
                new PathFinder(),
                Shop.createDefaultBarShop(),
                log,
                Game.createWelcomeNpcForRestore(save.interactives.welcomeNpcInteracted),
                Game.createBarSpecialNpcForRestore(save.interactives.barSpecialNpcInteracted),
                Game.createFriendNpcForRestore(save.interactives.friendNpcInteracted),
                Game.createDangerousCompanionForRestore());
        applyCollectedObjects(map, save.collectedObjectIds);
        applyEnemies(map, save.enemies);
        placePlayerOnMap(game);
        if (player.isFriendRescued()) {
            map.getRoom(game.getFriendNpc().getRoomId()).setCellType(game.getFriendNpc().getPosition(), CellType.EMPTY);
        }
        return game;
    }

    private Player createPlayer(PersistenceData.PlayerData data) {
        Player player = new Player(data.maxHealth, data.attack, data.defense, data.movementPoints,
                data.currentRoomId, new Position(data.row, data.column));
        player.setCurrentHealth(data.currentHealth);
        player.addChips(data.chips);
        if (data.friendRescued) {
            player.rescueFriend();
        }
        return player;
    }

    private Inventory createInventory(PersistenceData.InventoryData data) {
        Inventory inventory = new Inventory();
        if (data.items != null) {
            for (int i = 0; i < data.items.length; i++) {
                inventory.addItem(createItem(data.items[i]));
            }
        }
        inventory.restoreEquippedWeapon(data.equippedWeaponId);
        inventory.restoreEquippedArmor(data.equippedArmorId);
        if (data.activeEffects != null) {
            for (int i = 0; i < data.activeEffects.length; i++) {
                inventory.restoreActiveEffect(createEffect(data.activeEffects[i]));
            }
        }
        return inventory;
    }

    private Item createItem(PersistenceData.ItemData data) {
        ItemType type = ItemType.valueOf(data.type);
        if (type == ItemType.WEAPON) {
            return new Weapon(data.id, data.name, data.attackBonus);
        }
        if (type == ItemType.ARMOR) {
            return new Armor(data.id, data.name, data.defenseBonus, data.attackBonus);
        }
        if (type == ItemType.CONSUMABLE) {
            return new Consumable(data.id, data.name, createEffect(data.effect));
        }
        if (type == ItemType.KEY) {
            return new KeyItem(data.id, data.name);
        }
        throw new InvalidConfigurationException("Unsupported item type: " + data.type);
    }

    private Effect createEffect(PersistenceData.EffectData data) {
        return new Effect(EffectType.valueOf(data.type), data.amount, data.remainingTurns);
    }

    private GameLog createLog(PersistenceData.LogEntryData[] data) {
        GameLog log = new GameLog();
        if (data == null) {
            return log;
        }
        for (int i = 0; i < data.length; i++) {
            log.add(data[i].turn, data[i].message);
        }
        return log;
    }

    private void placePlayerOnMap(Game game) {
        CasinoMap map = game.getMap();
        map.getRoom(map.getInitialRoomId()).setCellType(map.getInitialPlayerPosition(), CellType.EMPTY);
        map.getRoom(game.getPlayer().getCurrentRoomId()).setCellType(game.getPlayer().getPosition(), CellType.PLAYER);
    }

    private void applyCollectedObjects(CasinoMap map, String[] collectedObjectIds) {
        if (collectedObjectIds == null) {
            return;
        }
        for (int i = 0; i < collectedObjectIds.length; i++) {
            removeRoomItem(map, collectedObjectIds[i]);
        }
    }

    private void removeRoomItem(CasinoMap map, String itemId) {
        if (itemId == null || itemId.isBlank()) {
            return;
        }
        for (int roomId = 1; roomId <= CasinoMap.EXIT_ROOM_ID; roomId++) {
            if (map.getRoom(roomId).removeItemById(itemId)) {
                return;
            }
        }
    }

    private void applyEnemies(CasinoMap map, PersistenceData.EnemySaveData[] enemies) {
        if (enemies == null) {
            return;
        }
        for (int i = 0; i < enemies.length; i++) {
            PersistenceData.EnemySaveData enemyData = enemies[i];
            Room room = map.getRoom(enemyData.roomId);
            Enemy enemy = room.findEnemyById(enemyData.id);
            if (enemy == null) {
                continue;
            }
            if (!enemyData.alive) {
                room.removeEnemy(enemy);
                continue;
            }
            Position restoredPosition = new Position(enemyData.row, enemyData.column);
            if (!enemy.getPosition().equals(restoredPosition)) {
                room.moveEnemy(enemy, restoredPosition);
            }
            enemy.setCurrentHealth(enemyData.currentHealth);
            if (enemyData.rewardClaimed) {
                enemy.markRewardClaimed();
            }
        }
    }
}
