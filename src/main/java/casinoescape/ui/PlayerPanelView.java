package casinoescape.ui;

import casinoescape.game.Game;
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
    private final Label friend = new Label();
    private final Label state = new Label();

    public PlayerPanelView() {
        root.setStyle("-fx-padding: 12; -fx-border-color: #f39c12; -fx-border-width: 3; -fx-background-color: #fffaf0;");
        Label title = new Label("Jugador");
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        root.getChildren().addAll(title, room, health, attack, defense, movement, chips, turns, friend, state);
    }

    public Node getNode() {
        return root;
    }

    public void refresh(Game game) {
        Player player = game.getPlayer();
        room.setText("Sala: " + player.getCurrentRoomId() + " - " + game.getCurrentRoom().getName());
        health.setText("Vida: " + player.getCurrentHealth() + "/" + player.getMaxHealth());
        attack.setText("Ataque: " + player.getAttack());
        defense.setText("Defensa: " + player.getDefense());
        movement.setText("Movimiento: " + player.getMovementPoints());
        chips.setText("Fichas: " + player.getChips());
        turns.setText("Turnos: " + game.getTurnManager().getTurnsRemaining());
        friend.setText("Amigo: " + (player.isFriendRescued() ? "rescatado" : "pendiente"));
        state.setText("Estado: " + game.getState());
    }
}
