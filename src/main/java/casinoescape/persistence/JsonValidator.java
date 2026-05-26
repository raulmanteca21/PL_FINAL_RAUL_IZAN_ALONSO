package casinoescape.persistence;

import casinoescape.exceptions.InvalidConfigurationException;
import casinoescape.game.CasinoMapBuilder;
import casinoescape.model.CasinoMap;
import casinoescape.model.CellType;
import casinoescape.model.GameState;
import casinoescape.model.Position;
import casinoescape.model.Room;
import casinoescape.items.EffectType;
import casinoescape.items.ItemType;

public class JsonValidator {
    private static final int FORMAT_VERSION = 1;

    public void validateConfig(PersistenceData.GameConfigData config) {
        if (config == null) {
            throw new InvalidConfigurationException("Configuration JSON is empty");
        }
        if (config.version != FORMAT_VERSION) {
            throw new InvalidConfigurationException("Unsupported configuration version");
        }
        if (config.initialTurns <= 0) {
            throw new InvalidConfigurationException("Initial turns must be positive");
        }
        if (config.initialRoomId != CasinoMapBuilder.INITIAL_ROOM_ID) {
            throw new InvalidConfigurationException("Initial room id does not match design");
        }
        requirePosition(config.initialPlayerPosition, "Initial player position is required");
        if (config.initialPlayerPosition.row != 3 || config.initialPlayerPosition.column != 3) {
            throw new InvalidConfigurationException("Initial player position does not match design");
        }
        validateRooms(config.rooms);
        validateConnections(config.connections);
        if (config.victory == null || config.victory.exitRoomId != CasinoMap.EXIT_ROOM_ID || !config.victory.requiresFriendRescued) {
            throw new InvalidConfigurationException("Victory configuration is invalid");
        }
    }

    public void validateSave(PersistenceData.SaveGameData save, CasinoMap map) {
        if (save == null) {
            throw new InvalidConfigurationException("Save JSON is empty");
        }
        if (save.version != FORMAT_VERSION) {
            throw new InvalidConfigurationException("Unsupported save version");
        }
        validatePlayer(save.player, map);
        validateTurn(save.turn, save.state);
        requireGameState(save.state);
        if (save.inventory == null) {
            throw new InvalidConfigurationException("Inventory data is required");
        }
        validateItems(save.inventory.items);
        validateEffects(save.inventory.activeEffects);
        if (save.interactives == null) {
            throw new InvalidConfigurationException("Interactives data is required");
        }
        validateEnemies(save.enemies, map);
        validateCollectedObjectIds(save.collectedObjectIds);
        validateDoors(save.doors);
        validateLog(save.log);
    }

    private void validateEnemies(PersistenceData.EnemySaveData[] enemies, CasinoMap map) {
        if (enemies == null) {
            return;
        }
        for (int i = 0; i < enemies.length; i++) {
            PersistenceData.EnemySaveData enemy = enemies[i];
            if (enemy == null || enemy.id == null || enemy.id.isBlank()) {
                throw new InvalidConfigurationException("Enemy save data is invalid");
            }
            Room room;
            try {
                room = map.getRoom(enemy.roomId);
            } catch (IllegalArgumentException exception) {
                throw new InvalidConfigurationException("Enemy room does not exist", exception);
            }
            if (!room.isInside(new Position(enemy.row, enemy.column))) {
                throw new InvalidConfigurationException("Enemy position is outside room");
            }
            if (enemy.currentHealth < 0) {
                throw new InvalidConfigurationException("Enemy health is invalid");
            }
            if (!isKnownEnemyState(enemy)) {
                throw new InvalidConfigurationException("Enemy save data does not match base enemies");
            }
            if (enemy.alive && enemy.currentHealth <= 0) {
                throw new InvalidConfigurationException("Alive enemy must have positive health");
            }
        }
    }

    private boolean isKnownEnemyState(PersistenceData.EnemySaveData enemy) {
        return isEnemy(enemy, CasinoMapBuilder.SLOT_MACHINE_ENEMY_ID, 2)
                || isEnemy(enemy, CasinoMapBuilder.BLACKJACK_DEALER_ENEMY_ID, 4)
                || isEnemy(enemy, CasinoMapBuilder.DRUNK_ENEMY_ID, 5)
                || isEnemy(enemy, CasinoMapBuilder.RUSSIAN_MAFIA_ENEMY_ID, 7)
                || isEnemy(enemy, CasinoMapBuilder.VIP_THUG_ENEMY_ID, 7);
    }

    private boolean isEnemy(PersistenceData.EnemySaveData enemy, String id, int roomId) {
        return id.equals(enemy.id) && enemy.roomId == roomId;
    }

    private void validateCollectedObjectIds(String[] collectedObjectIds) {
        if (collectedObjectIds == null) {
            return;
        }
        for (int i = 0; i < collectedObjectIds.length; i++) {
            if (collectedObjectIds[i] == null || collectedObjectIds[i].isBlank()) {
                throw new InvalidConfigurationException("Collected object id is invalid");
            }
        }
    }

    private void validateDoors(PersistenceData.DoorSaveData[] doors) {
        if (doors == null) {
            return;
        }
        for (int i = 0; i < doors.length; i++) {
            PersistenceData.DoorSaveData door = doors[i];
            if (door == null || door.fromRoomId <= 0 || door.toRoomId <= 0) {
                throw new InvalidConfigurationException("Door save data is invalid");
            }
            if (!((door.fromRoomId == 2 && door.toRoomId == 3) || (door.fromRoomId == 3 && door.toRoomId == 2))) {
                throw new InvalidConfigurationException("Only treasury door state is supported in current model");
            }
        }
    }

    private void validateRooms(PersistenceData.RoomConfigData[] rooms) {
        if (rooms == null || rooms.length != 8) {
            throw new InvalidConfigurationException("Configuration must contain 8 rooms");
        }
        for (int id = 1; id <= 8; id++) {
            PersistenceData.RoomConfigData room = findRoom(rooms, id);
            if (room == null) {
                throw new InvalidConfigurationException("Missing room " + id);
            }
            if (room.rows != CasinoMapBuilder.ROOM_SIZE || room.columns != CasinoMapBuilder.ROOM_SIZE) {
                throw new InvalidConfigurationException("Room dimensions must be 7x7");
            }
            if (room.name == null || room.name.isBlank()) {
                throw new InvalidConfigurationException("Room name is required");
            }
            validateCells(room);
        }
    }

    private void validateCells(PersistenceData.RoomConfigData room) {
        if (room.cells == null || room.cells.length == 0) {
            throw new InvalidConfigurationException("Room cells are required");
        }
        for (int i = 0; i < room.cells.length; i++) {
            PersistenceData.CellConfigData cell = room.cells[i];
            if (cell == null || cell.row < 0 || cell.column < 0 || cell.row >= room.rows || cell.column >= room.columns) {
                throw new InvalidConfigurationException("Room cell position is invalid");
            }
            CellType type = requireCellType(cell.type);
            if (type == CellType.DOOR && cell.destinationRoomId <= 0) {
                throw new InvalidConfigurationException("Door destination is required");
            }
            if (type == CellType.DOOR && cell.locked && (cell.requiredKeyName == null || cell.requiredKeyName.isBlank())) {
                throw new InvalidConfigurationException("Locked door key name is required");
            }
        }
    }

    private PersistenceData.RoomConfigData findRoom(PersistenceData.RoomConfigData[] rooms, int id) {
        for (int i = 0; i < rooms.length; i++) {
            if (rooms[i] != null && rooms[i].id == id) {
                return rooms[i];
            }
        }
        return null;
    }

    private void validateConnections(PersistenceData.ConnectionData[] connections) {
        if (connections == null || connections.length != 9) {
            throw new InvalidConfigurationException("Configuration must contain definitive graph connections");
        }
        requireConnection(connections, 1, 2);
        requireConnection(connections, 1, 4);
        requireConnection(connections, 2, 3);
        requireConnection(connections, 2, 5);
        requireConnection(connections, 4, 5);
        requireConnection(connections, 4, 6);
        requireConnection(connections, 5, 6);
        requireConnection(connections, 5, 7);
        requireConnection(connections, 7, 8);
    }

    private void requireConnection(PersistenceData.ConnectionData[] connections, int first, int second) {
        for (int i = 0; i < connections.length; i++) {
            PersistenceData.ConnectionData connection = connections[i];
            if (connection != null
                    && ((connection.from == first && connection.to == second)
                    || (connection.from == second && connection.to == first))) {
                return;
            }
        }
        throw new InvalidConfigurationException("Missing graph connection " + first + "-" + second);
    }

    private void validatePlayer(PersistenceData.PlayerData player, CasinoMap map) {
        if (player == null) {
            throw new InvalidConfigurationException("Player data is required");
        }
        if (player.maxHealth <= 0 || player.currentHealth < 0 || player.currentHealth > player.maxHealth) {
            throw new InvalidConfigurationException("Player health is invalid");
        }
        if (player.attack < 0 || player.defense < 0 || player.movementPoints <= 0 || player.chips < 0) {
            throw new InvalidConfigurationException("Player stats are invalid");
        }
        if (player.row < 0 || player.column < 0) {
            throw new InvalidConfigurationException("Player position is outside current room");
        }
        Room room;
        try {
            room = map.getRoom(player.currentRoomId);
        } catch (IllegalArgumentException exception) {
            throw new InvalidConfigurationException("Player room does not exist", exception);
        }
        if (!room.isInside(new Position(player.row, player.column))) {
            throw new InvalidConfigurationException("Player position is outside current room");
        }
    }

    private void validateTurn(PersistenceData.TurnData turn, String state) {
        if (turn == null) {
            throw new InvalidConfigurationException("Turn data is required");
        }
        if (turn.turnsRemaining < 0) {
            throw new InvalidConfigurationException("Turns remaining cannot be negative");
        }
        if (turn.turnsRemaining == 0 && !"DEFEAT".equals(state)) {
            throw new InvalidConfigurationException("Zero turns are only valid in defeat state");
        }
        requireTurnPhase(turn.phase);
    }

    private void validateItems(PersistenceData.ItemData[] items) {
        if (items == null) {
            return;
        }
        for (int i = 0; i < items.length; i++) {
            PersistenceData.ItemData item = items[i];
            if (item == null || item.id == null || item.id.isBlank() || item.name == null || item.name.isBlank()) {
                throw new InvalidConfigurationException("Inventory item is invalid");
            }
            requireItemType(item.type);
            if (item.attackBonus < 0 || item.defenseBonus < 0) {
                throw new InvalidConfigurationException("Item bonuses cannot be negative");
            }
            if ("CONSUMABLE".equals(item.type)) {
                validateEffect(item.effect);
            }
        }
    }

    private void validateEffects(PersistenceData.EffectData[] effects) {
        if (effects == null) {
            return;
        }
        for (int i = 0; i < effects.length; i++) {
            validateEffect(effects[i]);
        }
    }

    private void validateEffect(PersistenceData.EffectData effect) {
        if (effect == null) {
            throw new InvalidConfigurationException("Effect is required");
        }
        requireEffectType(effect.type);
        if (effect.amount < 0 || effect.remainingTurns < 0) {
            throw new InvalidConfigurationException("Effect values cannot be negative");
        }
    }

    private void validateLog(PersistenceData.LogEntryData[] log) {
        if (log == null) {
            return;
        }
        for (int i = 0; i < log.length; i++) {
            if (log[i] == null || log[i].message == null || log[i].message.isBlank()) {
                throw new InvalidConfigurationException("Log entry is invalid");
            }
        }
    }

    private void requirePosition(PersistenceData.PositionData position, String message) {
        if (position == null || position.row < 0 || position.column < 0) {
            throw new InvalidConfigurationException(message);
        }
    }

    private void requireGameState(String state) {
        try {
            GameState.valueOf(state);
        } catch (RuntimeException exception) {
            throw new InvalidConfigurationException("Game state is invalid", exception);
        }
    }

    private void requireTurnPhase(String phase) {
        try {
            casinoescape.game.TurnPhase.valueOf(phase);
        } catch (RuntimeException exception) {
            throw new InvalidConfigurationException("Turn phase is invalid", exception);
        }
    }

    private void requireItemType(String type) {
        try {
            ItemType.valueOf(type);
        } catch (RuntimeException exception) {
            throw new InvalidConfigurationException("Item type is invalid", exception);
        }
    }

    private void requireEffectType(String type) {
        try {
            EffectType.valueOf(type);
        } catch (RuntimeException exception) {
            throw new InvalidConfigurationException("Effect type is invalid", exception);
        }
    }

    private CellType requireCellType(String type) {
        try {
            return CellType.valueOf(type);
        } catch (RuntimeException exception) {
            throw new InvalidConfigurationException("Cell type is invalid", exception);
        }
    }
}
