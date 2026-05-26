package casinoescape.persistence;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import casinoescape.exceptions.PersistenceException;
import casinoescape.game.CasinoMapBuilder;
import casinoescape.game.Game;
import casinoescape.game.TurnManager;
import casinoescape.items.Inventory;
import casinoescape.items.Shop;
import casinoescape.logging.GameLog;
import casinoescape.model.CasinoMap;
import casinoescape.model.Cell;
import casinoescape.model.CellType;
import casinoescape.model.Door;
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
        Player player = new Player(100, 10, 5, 3, config.initialRoomId,
                new Position(config.initialPlayerPosition.row, config.initialPlayerPosition.column));
        return new Game(
                map,
                player,
                new Inventory(),
                new TurnManager(config.initialTurns),
                new MovementService(),
                new PathFinder(),
                Shop.createDefaultBarShop(),
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
