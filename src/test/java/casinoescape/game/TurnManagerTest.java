package casinoescape.game;

import casinoescape.model.GameState;
import casinoescape.model.Player;
import casinoescape.model.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TurnManagerTest {
    @Test
    void playerCanMoveAndActAtStartOfTurn() {
        TurnManager turnManager = new TurnManager(5);

        assertTrue(turnManager.canMove());
        assertTrue(turnManager.canAct());
        assertEquals(TurnPhase.PLAYER_TURN, turnManager.getPhase());
        assertEquals(GameState.IN_PROGRESS, turnManager.getGameState());
    }

    @Test
    void playerCannotMoveTwiceInSameTurn() {
        TurnManager turnManager = new TurnManager(5);

        turnManager.registerMovement();

        assertTrue(turnManager.hasMovementBeenUsed());
        assertFalse(turnManager.canMove());
        assertThrows(IllegalStateException.class, turnManager::registerMovement);
    }

    @Test
    void playerCannotActTwiceInSameTurn() {
        TurnManager turnManager = new TurnManager(5);

        turnManager.registerAction();

        assertTrue(turnManager.hasActionBeenUsed());
        assertFalse(turnManager.canAct());
        assertThrows(IllegalStateException.class, turnManager::registerAction);
    }

    @Test
    void playerCanActWithoutMoving() {
        TurnManager turnManager = new TurnManager(5);

        turnManager.registerAction();

        assertTrue(turnManager.hasActionBeenUsed());
        assertFalse(turnManager.hasMovementBeenUsed());
    }

    @Test
    void playerCanMoveWithoutActing() {
        TurnManager turnManager = new TurnManager(5);

        turnManager.registerMovement();

        assertTrue(turnManager.hasMovementBeenUsed());
        assertFalse(turnManager.hasActionBeenUsed());
        assertTrue(turnManager.canAct());
    }

    @Test
    void actionPreventsLaterMovementBecauseMovementGoesFirst() {
        TurnManager turnManager = new TurnManager(5);

        turnManager.registerAction();

        assertFalse(turnManager.canMove());
        assertThrows(IllegalStateException.class, turnManager::registerMovement);
    }

    @Test
    void endingTurnResetsMovementAndActionAndReducesTurns() {
        TurnManager turnManager = new TurnManager(5);
        turnManager.registerMovement();
        turnManager.registerAction();

        turnManager.endTurn(playerWithHealth(100));

        assertEquals(4, turnManager.getTurnsRemaining());
        assertFalse(turnManager.hasMovementBeenUsed());
        assertFalse(turnManager.hasActionBeenUsed());
        assertTrue(turnManager.canMove());
        assertTrue(turnManager.canAct());
    }

    @Test
    void endingTurnProcessesEnemyPhasePlaceholder() {
        TurnManager turnManager = new TurnManager(5);

        turnManager.endTurn(playerWithHealth(100));

        assertTrue(turnManager.wasEnemyPhaseProcessedLastTurn());
        assertEquals(TurnPhase.PLAYER_TURN, turnManager.getPhase());
    }

    @Test
    void turnsReachingZeroCauseDefeat() {
        TurnManager turnManager = new TurnManager(1);

        turnManager.endTurn(playerWithHealth(100));

        assertEquals(0, turnManager.getTurnsRemaining());
        assertEquals(GameState.DEFEAT, turnManager.getGameState());
        assertFalse(turnManager.canMove());
        assertFalse(turnManager.canAct());
    }

    @Test
    void zeroHealthCausesDefeat() {
        TurnManager turnManager = new TurnManager(5);
        Player player = playerWithHealth(100);
        player.setCurrentHealth(0);

        turnManager.checkDefeatByHealth(player);

        assertEquals(GameState.DEFEAT, turnManager.getGameState());
        assertFalse(turnManager.canMove());
        assertFalse(turnManager.canAct());
    }

    @Test
    void endingTurnWithDeadPlayerCausesDefeatWithoutReducingTurns() {
        TurnManager turnManager = new TurnManager(5);
        Player player = playerWithHealth(100);
        player.setCurrentHealth(0);

        turnManager.endTurn(player);

        assertEquals(GameState.DEFEAT, turnManager.getGameState());
        assertEquals(5, turnManager.getTurnsRemaining());
    }

    @Test
    void roomChangeFinishesTurn() {
        TurnManager turnManager = new TurnManager(5);
        turnManager.registerMovement();

        turnManager.finishTurnAfterRoomChange(playerWithHealth(100));

        assertEquals(4, turnManager.getTurnsRemaining());
        assertFalse(turnManager.hasMovementBeenUsed());
        assertFalse(turnManager.hasActionBeenUsed());
        assertTrue(turnManager.wasEnemyPhaseProcessedLastTurn());
    }

    @Test
    void invalidArgumentsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new TurnManager(0));
        assertThrows(IllegalArgumentException.class, () -> new TurnManager(-1));

        TurnManager turnManager = new TurnManager(5);
        assertThrows(IllegalArgumentException.class, () -> turnManager.endTurn(null));
        assertThrows(IllegalArgumentException.class, () -> turnManager.checkDefeatByHealth(null));
    }

    private Player playerWithHealth(int health) {
        Player player = new Player(100, 10, 5, 3, 1, new Position(3, 3));
        player.setCurrentHealth(health);
        return player;
    }
}
