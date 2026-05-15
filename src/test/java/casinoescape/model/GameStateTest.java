package casinoescape.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameStateTest {
    @Test
    void gameStatesAreDefined() {
        assertEquals(GameState.IN_PROGRESS, GameState.valueOf("IN_PROGRESS"));
        assertEquals(GameState.VICTORY, GameState.valueOf("VICTORY"));
        assertEquals(GameState.DEFEAT, GameState.valueOf("DEFEAT"));
        assertEquals(GameState.PAUSED, GameState.valueOf("PAUSED"));
    }
}
