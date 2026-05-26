package casinoescape.game;

import casinoescape.items.EffectType;
import casinoescape.items.Item;
import casinoescape.model.CellType;
import casinoescape.model.GameState;
import casinoescape.model.Position;
import casinoescape.model.Room;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameInteractivesTest {
    @Test
    void welcomeNpcReturnsMessageAndLogsInteraction() {
        Game game = Game.createNewGame(30);
        placePlayerInRoom(game, 1, new Position(3, 4));

        String message = game.interactWelcomeNpc();

        assertEquals(Game.WELCOME_MESSAGE, message);
        assertEquals(GameState.IN_PROGRESS, game.getState());
        assertEquals(1, game.getLog().size());
        assertTrue(game.getWelcomeNpc().hasAlreadyInteracted());
    }

    @Test
    void barSpecialNpcGivesSuspiciousPillOnlyOnce() {
        Game game = Game.createNewGame(30);
        placePlayerInRoom(game, 5, new Position(2, 2));

        Item firstItem = game.interactBarSpecialNpc();
        game.endTurn();
        Item secondItem = game.interactBarSpecialNpc();

        assertEquals("SUSPICIOUS_PILL", firstItem.getId());
        assertNull(secondItem);
        assertEquals(1, game.getInventory().size());
        game.getInventory().useConsumable("SUSPICIOUS_PILL", game.getPlayer());
        assertTrue(game.getInventory().hasActiveEffect(EffectType.LINE_MOVEMENT));
        assertEquals(7, game.getInventory().getActiveEffectTurns(EffectType.LINE_MOVEMENT));
    }

    @Test
    void rescuingFriendActivatesFriendFlagAndClearsCell() {
        Game game = Game.createNewGame(30);
        placePlayerInRoom(game, 6, new Position(3, 1));

        String message = game.rescueFriend();

        assertEquals(Game.FRIEND_RESCUED_MESSAGE, message);
        assertTrue(game.getPlayer().isFriendRescued());
        assertEquals(CellType.EMPTY, game.getMap().getRoom(6).getCell(CasinoMapBuilder.FRIEND_POSITION).getType());
        assertEquals(1, game.getLog().size());
    }

    @Test
    void dangerousCompanionDrainsLifeInOrthogonalRange() {
        Game game = Game.createNewGame(30);
        placePlayerInRoom(game, 6, new Position(3, 5));

        int damage = game.applyDangerousCompanionEffectIfInRange();

        assertEquals(10, damage);
        assertEquals(90, game.getPlayer().getCurrentHealth());
        assertEquals(GameState.IN_PROGRESS, game.getState());
        assertEquals(1, game.getLog().size());
    }

    @Test
    void dangerousCompanionDoesNotDrainOutsideOrthogonalRangeOrDiagonal() {
        Game outsideRange = Game.createNewGame(30);
        placePlayerInRoom(outsideRange, 6, new Position(2, 2));

        assertEquals(0, outsideRange.applyDangerousCompanionEffectIfInRange());
        assertEquals(100, outsideRange.getPlayer().getCurrentHealth());

        Game diagonal = Game.createNewGame(30);
        placePlayerInRoom(diagonal, 6, new Position(2, 5));

        assertEquals(0, diagonal.applyDangerousCompanionEffectIfInRange());
        assertEquals(100, diagonal.getPlayer().getCurrentHealth());
    }

    @Test
    void dangerousCompanionCanCauseDefeat() {
        Game game = Game.createNewGame(30);
        placePlayerInRoom(game, 6, new Position(3, 5));
        game.getPlayer().setCurrentHealth(5);

        int damage = game.applyDangerousCompanionEffectIfInRange();

        assertEquals(10, damage);
        assertEquals(0, game.getPlayer().getCurrentHealth());
        assertEquals(GameState.DEFEAT, game.getState());
    }

    @Test
    void decliningRussianRouletteDoesNotChangeLifeChipsOrState() {
        Game game = Game.createNewGame(30);
        placePlayerInRoom(game, 8, new Position(3, 5));

        RouletteResult result = game.playRussianRoulette(false, 0.9);

        assertFalse(result.wasPlayed());
        assertEquals(100, game.getPlayer().getCurrentHealth());
        assertEquals(0, game.getPlayer().getChips());
        assertEquals(GameState.IN_PROGRESS, game.getState());
    }

    @Test
    void favorableRussianRouletteAwardsChips() {
        Game game = Game.createNewGame(30);
        placePlayerInRoom(game, 8, new Position(3, 5));

        RouletteResult result = game.playRussianRoulette(true, 0.49);

        assertTrue(result.wasPlayed());
        assertTrue(result.isFavorable());
        assertEquals(Game.ROULETTE_REWARD_CHIPS, result.getChipsAwarded());
        assertEquals(Game.ROULETTE_REWARD_CHIPS, game.getPlayer().getChips());
        assertEquals(GameState.IN_PROGRESS, game.getState());
    }

    @Test
    void unfavorableRussianRouletteIsLethalAndBoundaryIsUnfavorable() {
        Game game = Game.createNewGame(30);
        placePlayerInRoom(game, 8, new Position(3, 5));

        RouletteResult result = game.playRussianRoulette(true, 0.5);

        assertTrue(result.wasPlayed());
        assertFalse(result.isFavorable());
        assertTrue(result.isLethal());
        assertEquals(100, result.getDamageTaken());
        assertEquals(0, game.getPlayer().getCurrentHealth());
        assertEquals(GameState.DEFEAT, game.getState());
    }

    @Test
    void russianRouletteRequiresAdjacencyToMinigameCell() {
        Game game = Game.createNewGame(30);
        placePlayerInRoom(game, 8, new Position(6, 6));

        assertThrows(IllegalStateException.class, () -> game.playRussianRoulette(true, 0.1));
    }

    @Test
    void exitRequiresFriendAndAllowsVictoryWithFriend() {
        Game withoutFriend = Game.createNewGame(30);
        placePlayerInRoom(withoutFriend, 8, new Position(0, 4));

        assertEquals(Game.EXIT_WITHOUT_FRIEND_MESSAGE, withoutFriend.interactExit());
        assertEquals(GameState.IN_PROGRESS, withoutFriend.getState());

        Game withFriend = Game.createNewGame(30);
        placePlayerInRoom(withFriend, 8, new Position(0, 4));
        withFriend.getPlayer().rescueFriend();

        assertEquals(Game.VICTORY_MESSAGE, withFriend.interactExit());
        assertEquals(GameState.VICTORY, withFriend.getState());
    }

    @Test
    void exitRequiresAdjacencyToExitCell() {
        Game game = Game.createNewGame(30);
        placePlayerInRoom(game, 8, new Position(6, 6));
        game.getPlayer().rescueFriend();

        assertThrows(IllegalStateException.class, game::interactExit);
    }

    @Test
    void gameEndTurnAppliesDangerousCompanionEffect() {
        Game game = Game.createNewGame(30);
        placePlayerInRoom(game, 6, new Position(3, 5));

        game.endTurn();

        assertEquals(90, game.getPlayer().getCurrentHealth());
        assertEquals(29, game.getTurnManager().getTurnsRemaining());
    }

    private void placePlayerInRoom(Game game, int roomId, Position position) {
        Room currentRoom = game.getCurrentRoom();
        currentRoom.setCellType(game.getPlayer().getPosition(), CellType.EMPTY);
        game.getPlayer().setCurrentRoomId(roomId);
        game.getPlayer().setPosition(position);
        game.getCurrentRoom().setCellType(position, CellType.PLAYER);
    }
}
