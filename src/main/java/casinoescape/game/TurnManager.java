package casinoescape.game;

import casinoescape.model.GameState;
import casinoescape.model.Player;

public class TurnManager {
    private int turnsRemaining;
    private boolean movementUsed;
    private boolean actionUsed;
    private boolean enemyPhaseProcessedLastTurn;
    private TurnPhase phase;
    private GameState gameState;

    public TurnManager(int turnsRemaining) {
        if (turnsRemaining <= 0) {
            throw new IllegalArgumentException("Turns remaining must be positive");
        }
        this.turnsRemaining = turnsRemaining;
        this.phase = TurnPhase.PLAYER_TURN;
        this.gameState = GameState.IN_PROGRESS;
    }

    public boolean canMove() {
        return gameState == GameState.IN_PROGRESS
                && phase == TurnPhase.PLAYER_TURN
                && !movementUsed
                && !actionUsed;
    }

    public void registerMovement() {
        if (!canMove()) {
            throw new IllegalStateException("Movement is not available in this turn");
        }
        movementUsed = true;
    }

    public boolean canAct() {
        return gameState == GameState.IN_PROGRESS
                && phase == TurnPhase.PLAYER_TURN
                && !actionUsed;
    }

    public void registerAction() {
        if (!canAct()) {
            throw new IllegalStateException("Action is not available in this turn");
        }
        actionUsed = true;
    }

    public void endTurn(Player player) {
        requirePlayer(player);
        enemyPhaseProcessedLastTurn = false;

        if (player.getCurrentHealth() <= 0) {
            gameState = GameState.DEFEAT;
            return;
        }

        phase = TurnPhase.ENEMY_PHASE;
        processEnemyPhasePlaceholder();

        turnsRemaining--;
        if (turnsRemaining <= 0) {
            turnsRemaining = 0;
            gameState = GameState.DEFEAT;
            return;
        }

        startNextPlayerTurn();
    }

    public void finishTurnAfterRoomChange(Player player) {
        endTurn(player);
    }

    public void checkDefeatByHealth(Player player) {
        requirePlayer(player);
        if (player.getCurrentHealth() <= 0) {
            gameState = GameState.DEFEAT;
        }
    }

    public int getTurnsRemaining() {
        return turnsRemaining;
    }

    public boolean hasMovementBeenUsed() {
        return movementUsed;
    }

    public boolean hasActionBeenUsed() {
        return actionUsed;
    }

    public boolean wasEnemyPhaseProcessedLastTurn() {
        return enemyPhaseProcessedLastTurn;
    }

    public TurnPhase getPhase() {
        return phase;
    }

    public GameState getGameState() {
        return gameState;
    }

    private void startNextPlayerTurn() {
        movementUsed = false;
        actionUsed = false;
        phase = TurnPhase.PLAYER_TURN;
    }

    private void processEnemyPhasePlaceholder() {
        // Enemy movement and combat will be added in later modules.
        enemyPhaseProcessedLastTurn = true;
    }

    private void requirePlayer(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player is required");
        }
    }
}
