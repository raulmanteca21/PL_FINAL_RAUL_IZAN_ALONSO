package casinoescape.persistence;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import casinoescape.exceptions.PersistenceException;
import casinoescape.game.CasinoMapBuilder;
import casinoescape.game.Game;
import casinoescape.game.TurnManager;
import casinoescape.items.Armor;
import casinoescape.items.Consumable;
import casinoescape.items.Effect;
import casinoescape.items.EffectType;
import casinoescape.items.Inventory;
import casinoescape.items.Item;
import casinoescape.items.ItemType;
import casinoescape.items.KeyItem;
import casinoescape.items.Shop;
import casinoescape.items.ShopItem;
import casinoescape.items.Weapon;
import casinoescape.logging.GameLog;
import casinoescape.model.CasinoMap;
import casinoescape.model.Cell;
import casinoescape.model.CellType;
import casinoescape.model.Door;
import casinoescape.model.Enemy;
import casinoescape.model.Player;
import casinoescape.model.Position;
import casinoescape.model.Room;
import casinoescape.movement.MovementService;
import casinoescape.movement.PathFinder;
import casinoescape.structures.MyGraph;
import casinoescape.structures.MyLinkedList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GameConfigLoader {
    private final Gson gson;
    private final JsonValidator validator;

    public GameConfigLoader() {
        this(new Gson(), new JsonValidator());
    }

    public GameConfigLoader(Gson gson, JsonValidator validator) {
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
        PersistenceData.GameConfigData config = readConfig(path);
        validator.validateConfig(config);
        CasinoMap map = buildMap(config);
        Player player = new Player(Game.INITIAL_HEALTH, Game.INITIAL_ATTACK, Game.INITIAL_DEFENSE, Game.INITIAL_MOVEMENT, config.initialRoomId,
                new Position(config.initialPlayerPosition.row, config.initialPlayerPosition.column));
        return new Game(
                map,
                player,
                new Inventory(),
                new TurnManager(config.initialTurns),
                new MovementService(),
                new PathFinder(),
                buildShop(config.shop),
                new GameLog(),
                Game.createWelcomeNpcForRestore(false),
                Game.createBarSpecialNpcForRestore(false),
                Game.createFriendNpcForRestore(false),
                Game.createDangerousCompanionForRestore());
    }

    private CasinoMap buildMap(PersistenceData.GameConfigData config) {
        MyLinkedList<Room> rooms = new MyLinkedList<>();
        for (int roomId = 1; roomId <= 8; roomId++) {
            PersistenceData.RoomConfigData roomData = findRoom(config.rooms, roomId);
            Room room = new Room(roomData.id, roomData.name, roomData.rows, roomData.columns);
            applyCells(room, roomData.cells);
            applyItems(room, roomData.items);
            applyEnemies(room, roomData.enemies);
            rooms.add(room);
        }

        MyGraph<Integer> graph = new MyGraph<>();
        for (int roomId = 1; roomId <= 8; roomId++) {
            graph.addNode(roomId);
        }
        for (int i = 0; i < config.connections.length; i++) {
            graph.addUndirectedEdge(config.connections[i].from, config.connections[i].to);
        }

        return new CasinoMap(graph, rooms, config.initialRoomId,
                new Position(config.initialPlayerPosition.row, config.initialPlayerPosition.column));
    }

    private PersistenceData.RoomConfigData findRoom(PersistenceData.RoomConfigData[] rooms, int roomId) {
        for (int i = 0; i < rooms.length; i++) {
            if (rooms[i].id == roomId) {
                return rooms[i];
            }
        }
        throw new PersistenceException("Validated room not found: " + roomId);
    }

    private void applyCells(Room room, PersistenceData.CellConfigData[] cells) {
        if (cells == null) {
            return;
        }
        for (int i = 0; i < cells.length; i++) {
            PersistenceData.CellConfigData cell = cells[i];
            Position position = new Position(cell.row, cell.column);
            if (CellType.DOOR.name().equals(cell.type)) {
                Door door = cell.locked
                        ? new Door(cell.destinationRoomId, true, cell.requiredKeyName)
                        : new Door(cell.destinationRoomId);
                room.setCell(position, new Cell(door, cell.label));
            } else {
                room.setCell(position, new Cell(CellType.valueOf(cell.type), cell.label));
            }
        }
    }

    private void applyItems(Room room, PersistenceData.RoomItemConfigData[] items) {
        if (items == null) {
            return;
        }
        for (int i = 0; i < items.length; i++) {
            PersistenceData.RoomItemConfigData item = items[i];
            room.addItem(createItem(item), new Position(item.row, item.column));
        }
    }

    private void applyEnemies(Room room, PersistenceData.EnemyConfigData[] enemies) {
        if (enemies == null) {
            return;
        }
        for (int i = 0; i < enemies.length; i++) {
            PersistenceData.EnemyConfigData enemy = enemies[i];
            room.addEnemy(new Enemy(enemy.id, enemy.name, enemy.maxHealth, enemy.attack, enemy.defense,
                    new Position(enemy.row, enemy.column), enemy.chipReward, enemy.dropName));
        }
    }

    private Shop buildShop(PersistenceData.ShopItemConfigData[] shopItems) {
        if (shopItems == null) {
            return Shop.createDefaultBarShop();
        }
        Shop shop = new Shop();
        for (int i = 0; i < shopItems.length; i++) {
            PersistenceData.ShopItemConfigData shopItem = shopItems[i];
            shop.addItem(new ShopItem(shopItem.shopItemId, shopItem.name, shopItem.price, createItem(shopItem)));
        }
        return shop;
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
        throw new PersistenceException("Unsupported item type: " + data.type);
    }

    private Effect createEffect(PersistenceData.EffectData data) {
        return new Effect(EffectType.valueOf(data.type), data.amount, data.remainingTurns);
    }

    private PersistenceData.GameConfigData readConfig(Path path) {
        if (path == null) {
            throw new IllegalArgumentException("Configuration path is required");
        }
        if (!Files.exists(path)) {
            throw new PersistenceException("Configuration file does not exist: " + path);
        }
        try {
            return gson.fromJson(Files.readString(path), PersistenceData.GameConfigData.class);
        } catch (JsonSyntaxException exception) {
            throw new PersistenceException("Configuration JSON is malformed", exception);
        } catch (IOException exception) {
            throw new PersistenceException("Could not read configuration file", exception);
        }
    }
}
