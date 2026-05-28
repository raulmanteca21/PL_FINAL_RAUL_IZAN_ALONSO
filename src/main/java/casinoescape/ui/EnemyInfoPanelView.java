package casinoescape.ui;

import casinoescape.model.Enemy;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class EnemyInfoPanelView {
    private final VBox root = new VBox(6);
    private final Label name = new Label("Enemigo: ninguno seleccionado");
    private final Label health = new Label("Vida: -");
    private final Label attack = new Label("Ataque: -");
    private final Label defense = new Label("Defensa/Escudo: -");
    private final Label movement = new Label("Movimiento: -");
    private final Label reward = new Label("Recompensa: -");
    private final Label hint = new Label("Click en un enemigo para ver sus datos.");

    public EnemyInfoPanelView() {
        root.setStyle("-fx-padding: 12; -fx-border-color: #9E1B1B, #D4AF37; -fx-border-width: 2, 3; -fx-background-color: #FFF4D6;");
        Label title = new Label("♥ Mesa de enemigos");
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #5A1717;");
        styleInfoLabel(name);
        styleInfoLabel(health);
        styleInfoLabel(attack);
        styleInfoLabel(defense);
        styleInfoLabel(movement);
        hint.setWrapText(true);
        reward.setWrapText(true);
        reward.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #8C6A21;");
        hint.setStyle("-fx-font-size: 12; -fx-text-fill: #4A4030;");
        root.getChildren().addAll(title, name, health, attack, defense, movement, reward, hint);
    }

    public Node getNode() {
        return root;
    }

    public void refresh(Enemy enemy) {
        if (enemy == null) {
            name.setText("Enemigo: ninguno seleccionado");
            health.setText("Vida: -");
            attack.setText("Ataque: -");
            defense.setText("Defensa/Escudo: -");
            movement.setText("Movimiento: -");
            reward.setText("Recompensa: -");
            hint.setText("Click en un enemigo para ver sus datos.");
            return;
        }
        name.setText("Enemigo: " + enemy.getName());
        health.setText("♥ Vida: " + enemy.getCurrentHealth() + "/" + enemy.getMaxHealth());
        attack.setText("♦ Ataque: " + enemy.getAttack());
        defense.setText("♣ Escudo: " + enemy.getDefense());
        movement.setText("Movimiento: aproximacion por BFS");
        reward.setText("Recompensa: " + formatReward(enemy));
        hint.setText("Ver estadisticas no consume accion. Para combatir usa el boton Atacar.");
    }

    private void styleInfoLabel(Label label) {
        label.setWrapText(true);
        label.setStyle("-fx-font-size: 13; -fx-text-fill: #17130A;");
    }

    private String formatReward(Enemy enemy) {
        String text = enemy.getChipReward() + " fichas";
        if (!enemy.getDropName().isBlank()) {
            text += ", " + enemy.getDropName();
        }
        return text;
    }
}
