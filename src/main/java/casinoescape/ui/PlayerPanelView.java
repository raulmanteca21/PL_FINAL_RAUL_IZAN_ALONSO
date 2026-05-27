package casinoescape.ui;

import casinoescape.game.Game;
import casinoescape.model.GameState;
import casinoescape.model.Player;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class PlayerPanelView {
    private final VBox root = new VBox(6);
    private final Label room = new Label();
    private final Label health = new Label();
    private final Label attack = new Label();
    private final Label defense = new Label();
    private final Label movement = new Label();
    private final Label chips = new Label();
    private final Label turns = new Label();
    private final Label result = new Label();

    public PlayerPanelView() {
        root.setStyle("-fx-padding: 12; -fx-border-color: #d4af37; -fx-border-width: 3; -fx-background-color: #fff8df;");
        Label title = new Label("Mesa del jugador");
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #5c2300;");
        result.setVisible(false);
        result.setManaged(false);
        root.getChildren().addAll(title, room, health, attack, defense, movement, chips, turns, result);
    }

    public Node getNode() {
        return root;
    }

    public void refresh(Game game) {
        Player player = game.getPlayer();
        room.setText("Sala: " + player.getCurrentRoomId() + " - " + game.getCurrentRoom().getName());
        health.setText("Vida: " + player.getCurrentHealth() + "/" + player.getMaxHealth());
        attack.setText("Ataque: " + player.getAttack());
        defense.setText("Defensa/Escudo: " + player.getDefense());
        movement.setText("Movimiento: " + player.getMovementPoints());
        chips.setText("Fichas: " + player.getChips());
        turns.setText("Turnos: " + game.getTurnManager().getTurnsRemaining());
        refreshResult(game.getState());
    }

    private void refreshResult(GameState state) {
        boolean finished = state != GameState.IN_PROGRESS;
        result.setVisible(finished);
        result.setManaged(finished);
        if (state == GameState.VICTORY) {
            result.setText("Resultado: Victoria");
        } else if (state == GameState.DEFEAT) {
            result.setText("Resultado: Derrota");
        } else {
            result.setText("");
        }
    }
}
