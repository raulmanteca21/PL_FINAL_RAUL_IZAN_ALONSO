package casinoescape.persistence;

import casinoescape.exceptions.InvalidConfigurationException;
import casinoescape.exceptions.PersistenceException;
import casinoescape.game.Game;
import casinoescape.game.TurnPhase;
import casinoescape.items.Armor;
import casinoescape.items.Consumable;
import casinoescape.items.Effect;
import casinoescape.items.EffectType;
import casinoescape.items.KeyItem;
import casinoescape.items.Shop;
import casinoescape.items.Weapon;
import casinoescape.model.CellType;
import casinoescape.model.GameState;
import casinoescape.model.Position;
import casinoescape.model.Room;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersistenceJsonTest {
    @TempDir
    Path tempDir;

    @Test
    void loadValidInitialConfigurationCreatesExpectedGameWorld() {
        Game game = new GameConfigLoader().load(Path.of("config", "game_config.json"));

        assertEquals(8, game.getMap().getRoomCount());
        assertEquals(1, game.getPlayer().getCurrentRoomId());
        assertEquals(new Position(3, 3), game.getPlayer().getPosition());
        assertEquals(30, game.getTurnManager().getTurnsRemaining());
        assertTrue(game.getMap().areRoomsConnected(1, 2));
        assertTrue(game.getMap().areRoomsConnected(5, 7));
        assertTrue(game.getMap().roomHasCellType(8, CellType.EXIT));
    }

    @Test
    void loadInitialConfigurationMarksTreasuryDoorLocked() {
        Game game = new GameConfigLoader().load(Path.of("config", "game_config.json"));

        assertTrue(game.getMap().getDoorTo(2, 3).isLocked());
        assertFalse(game.getMap().canTransition(2, 3, false));
        assertTrue(game.getMap().canTransition(2, 3, true));
    }

    @Test
    void invalidConfigurationThrowsInvalidConfigurationException() throws Exception {
        Path invalid = tempDir.resolve("invalid_config.json");
        Files.writeString(invalid, "{\"version\":1,\"initialTurns\":30}");

        assertThrows(InvalidConfigurationException.class, () -> new GameConfigLoader().load(invalid));
    }

    @Test
    void malformedConfigurationThrowsPersistenceException() throws Exception {
        Path malformed = tempDir.resolve("malformed_config.json");
        Files.writeString(malformed, "{ \"rooms\": [");

        assertThrows(PersistenceException.class, () -> new GameConfigLoader().load(malformed));
    }

    @Test
    void missingConfigurationThrowsPersistenceException() {
        assertThrows(PersistenceException.class, () -> new GameConfigLoader().load(tempDir.resolve("missing.json")));
    }

    @Test
    void saveGameStateCreatesReadableSaveFile() {
        Game game = Game.createNewGame(30);
        Path savePath = tempDir.resolve("savegame.json");

        new GameSaveWriter().save(game, savePath);

        assertTrue(Files.exists(savePath));
        assertTrue(Files.isRegularFile(savePath));
    }

    @Test
    void saveAndLoadRestoresCurrentRoomPositionStatsChipsFriendAndLog() {
        Game game = Game.createNewGame(30);
        placePlayerInRoom(game, 5, new Position(4, 4));
        game.getPlayer().setCurrentHealth(72);
        game.getPlayer().addChips(Shop.TREASURY_KEY_PRICE);
        game.buyFromBar(Shop.TREASURY_KEY_SHOP_ID);
        game.getPlayer().rescueFriend();
        game.getLog().add(4, "Evento de prueba");
        Path savePath = tempDir.resolve("savegame.json");

        new GameSaveWriter().save(game, savePath);
        Game loaded = new GameSaveLoader().load(savePath);

        assertEquals(5, loaded.getPlayer().getCurrentRoomId());
        assertEquals(new Position(4, 4), loaded.getPlayer().getPosition());
        assertEquals(72, loaded.getPlayer().getCurrentHealth());
        assertEquals(0, loaded.getPlayer().getChips());
        assertTrue(loaded.getInventory().hasTreasuryKey());
        assertTrue(loaded.getPlayer().isFriendRescued());
        assertEquals(game.getLog().size(), loaded.getLog().size());
        assertEquals("Evento de prueba", loaded.getLog().getEntry(loaded.getLog().size() - 1).getMessage());
    }

    @Test
    void saveAndLoadRestoresInventoryEquipmentAndActiveEffects() {
        Game game = Game.createNewGame(30);
        game.getInventory().addItem(new Weapon("STAFF", "Baston gitano", 6));
        game.getInventory().addItem(new Armor("SHIELD_SUIT", "Traje con escudo", 3));
        game.getInventory().addItem(new Consumable("PILL", "Pastilla de dudosa procedencia", new Effect(EffectType.LINE_MOVEMENT, 0, 7)));
        game.getInventory().equipWeapon("STAFF", game.getPlayer());
        game.getInventory().equipArmor("SHIELD_SUIT", game.getPlayer());
        game.getInventory().useConsumable("PILL", game.getPlayer());
        Path savePath = tempDir.resolve("equipped_save.json");

        new GameSaveWriter().save(game, savePath);
        Game loaded = new GameSaveLoader().load(savePath);

        assertEquals("STAFF", loaded.getInventory().getEquippedWeapon().getId());
        assertEquals("SHIELD_SUIT", loaded.getInventory().getEquippedArmor().getId());
        assertTrue(loaded.getInventory().hasActiveEffect(EffectType.LINE_MOVEMENT));
        assertEquals(7, loaded.getInventory().getActiveEffectTurns(EffectType.LINE_MOVEMENT));
        assertEquals(game.getPlayer().getAttack(), loaded.getPlayer().getAttack());
        assertEquals(game.getPlayer().getDefense(), loaded.getPlayer().getDefense());
    }

    @Test
    void saveAndLoadPreservesTurnStateAndGameState() {
        Game game = Game.createNewGame(30);
        game.movePlayer(new Position(4, 3));
        Path savePath = tempDir.resolve("turn_save.json");

        new GameSaveWriter().save(game, savePath);
        Game loaded = new GameSaveLoader().load(savePath);

        assertEquals(30, loaded.getTurnManager().getTurnsRemaining());
        assertTrue(loaded.getTurnManager().hasMovementBeenUsed());
        assertFalse(loaded.getTurnManager().hasActionBeenUsed());
        assertEquals(TurnPhase.PLAYER_TURN, loaded.getTurnManager().getPhase());
        assertEquals(GameState.IN_PROGRESS, loaded.getState());
    }

    @Test
    void loadSaveWithInvalidSemanticDataThrowsInvalidConfigurationException() throws Exception {
        Path invalid = tempDir.resolve("invalid_save.json");
        Files.writeString(invalid, "{\"version\":1,\"state\":\"IN_PROGRESS\",\"player\":{" +
                "\"currentRoomId\":99,\"row\":0,\"column\":0,\"maxHealth\":100,\"currentHealth\":100," +
                "\"attack\":1,\"defense\":1,\"movementPoints\":1,\"chips\":0,\"friendRescued\":false}," +
                "\"turn\":{\"turnsRemaining\":1,\"movementUsed\":false,\"actionUsed\":false," +
                "\"enemyPhaseProcessedLastTurn\":false,\"phase\":\"PLAYER_TURN\"}," +
                "\"inventory\":{\"items\":[],\"equippedWeaponId\":\"\",\"equippedArmorId\":\"\",\"activeEffects\":[]}," +
                "\"interactives\":{\"welcomeNpcInteracted\":false,\"barSpecialNpcInteracted\":false,\"friendNpcInteracted\":false}," +
                "\"log\":[]}");

        assertThrows(InvalidConfigurationException.class, () -> new GameSaveLoader().load(invalid));
    }

    @Test
    void loadSaveWithEnemyStateIsSupported() throws Exception {
        Path valid = tempDir.resolve("enemy_save.json");
        Files.writeString(valid, validSavePrefix()
                + ",\"enemies\":[{\"id\":\"SLOT_MACHINE_BROKEN\",\"roomId\":2,\"row\":3,\"column\":4,\"currentHealth\":0,\"alive\":false,\"rewardClaimed\":true}],"
                + "\"collectedObjectIds\":[],\"doors\":[],\"treasuryKeyBought\":false,\"log\":[]}");

        Game loaded = new GameSaveLoader().load(valid);

        assertEquals(0, loaded.getMap().getRoom(2).enemyCount());
    }

    @Test
    void loadSaveWithCollectedObjectsIsSupported() throws Exception {
        Path valid = tempDir.resolve("objects_save.json");
        Files.writeString(valid, validSavePrefix()
                + ",\"enemies\":[],\"collectedObjectIds\":[\"BROKEN_BOTTLE\"],"
                + "\"doors\":[],\"treasuryKeyBought\":false,\"log\":[]}");

        Game loaded = new GameSaveLoader().load(valid);

        assertEquals(null, loaded.getMap().getRoom(1).findItemPosition("BROKEN_BOTTLE"));
    }

    @Test
    void malformedSaveThrowsPersistenceException() throws Exception {
        Path malformed = tempDir.resolve("malformed_save.json");
        Files.writeString(malformed, "{ \"player\": [");

        assertThrows(PersistenceException.class, () -> new GameSaveLoader().load(malformed));
    }

    @Test
    void missingSaveThrowsPersistenceException() {
        assertThrows(PersistenceException.class, () -> new GameSaveLoader().load(tempDir.resolve("missing_save.json")));
    }

    private void placePlayerInRoom(Game game, int roomId, Position position) {
        Room currentRoom = game.getCurrentRoom();
        currentRoom.setCellType(game.getPlayer().getPosition(), CellType.EMPTY);
        game.getPlayer().setCurrentRoomId(roomId);
        game.getPlayer().setPosition(position);
        game.getCurrentRoom().setCellType(position, CellType.PLAYER);
    }

    private String validSavePrefix() {
        return "{\"version\":1,\"state\":\"IN_PROGRESS\",\"player\":{" +
                "\"currentRoomId\":1,\"row\":3,\"column\":3,\"maxHealth\":100,\"currentHealth\":100," +
                "\"attack\":10,\"defense\":5,\"movementPoints\":3,\"chips\":0,\"friendRescued\":false}," +
                "\"turn\":{\"turnsRemaining\":30,\"movementUsed\":false,\"actionUsed\":false," +
                "\"enemyPhaseProcessedLastTurn\":false,\"phase\":\"PLAYER_TURN\"}," +
                "\"inventory\":{\"items\":[],\"equippedWeaponId\":\"\",\"equippedArmorId\":\"\",\"activeEffects\":[]}," +
                "\"interactives\":{\"welcomeNpcInteracted\":false,\"barSpecialNpcInteracted\":false,\"friendNpcInteracted\":false}";
    }
}
