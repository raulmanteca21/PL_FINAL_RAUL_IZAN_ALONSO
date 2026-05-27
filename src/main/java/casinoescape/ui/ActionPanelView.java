package casinoescape.ui;

import casinoescape.game.Game;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;

public class ActionPanelView {
    private final FlowPane root = new FlowPane(8, 8);
    private final Button endTurn = new Button("Finalizar turno");
    private final Button attack = new Button("Atacar");
    private final Button pickItem = new Button("Recoger");
    private final Button lineMove = new Button("Movimiento linea");
    private final Button interact = new Button("Interactuar");
    private final Button useDoor = new Button("Usar puerta");
    private final Button useItem = new Button("Usar objeto");
    private final Button equipWeapon = new Button("Equipar arma");
    private final Button equipArmor = new Button("Equipar armadura");
    private final Button shop = new Button("Tienda");
    private final Button roulette = new Button("Ruleta");
    private final Button save = new Button("Guardar");
    private final Button load = new Button("Cargar");
    private final Button replay = new Button("Volver a jugar");

    public ActionPanelView() {
        root.setStyle("-fx-padding: 10; -fx-background-color: #141414; -fx-border-color: #d4af37; -fx-border-width: 2 0 0 0;");
        endTurn.setTooltip(new Tooltip("Termina tu turno y deja actuar a los enemigos"));
        attack.setTooltip(new Tooltip("Requiere enemigo adyacente. No se activa al hacer click en el enemigo"));
        pickItem.setTooltip(new Tooltip("Requiere objeto adyacente"));
        lineMove.setTooltip(new Tooltip("Requiere Pastilla de dudosa procedencia activa"));
        interact.setTooltip(new Tooltip("Interactua con NPC, objeto especial, salida o elemento adyacente"));
        useDoor.setTooltip(new Tooltip("Requiere puerta adyacente"));
        useItem.setTooltip(new Tooltip("Usa el objeto seleccionado del inventario"));
        equipWeapon.setTooltip(new Tooltip("Equipa el arma seleccionada"));
        equipArmor.setTooltip(new Tooltip("Equipa la armadura seleccionada"));
        shop.setTooltip(new Tooltip("Requiere estar junto al BAR"));
        roulette.setTooltip(new Tooltip("Requiere estar junto a RULETA"));
        save.setTooltip(new Tooltip("Guarda la partida actual"));
        load.setTooltip(new Tooltip("Carga la partida guardada"));
        replay.setTooltip(new Tooltip("Inicia una nueva partida desde config/game_config.json"));
        styleActionButtons();
        replay.setStyle("-fx-background-color: #d4af37; -fx-text-fill: #141414; -fx-font-weight: bold; -fx-border-color: #fff8df; -fx-border-width: 2;");
        replay.setVisible(false);
        replay.setManaged(false);
        root.getChildren().addAll(endTurn, attack, pickItem, lineMove, interact, useDoor, useItem, equipWeapon, equipArmor, shop, roulette, save, load, replay);
    }

    private void styleActionButtons() {
        Button[] buttons = {endTurn, attack, pickItem, lineMove, interact, useDoor, useItem, equipWeapon, equipArmor, shop, roulette, save, load};
        for (Button button : buttons) {
            button.setStyle("-fx-background-color: #fff8df; -fx-text-fill: #141414; -fx-font-weight: bold; -fx-border-color: #d4af37; -fx-border-width: 1;");
        }
    }

    public Node getNode() {
        return root;
    }

    public void setOnEndTurn(Runnable handler) { endTurn.setOnAction(event -> handler.run()); }
    public void setOnAttack(Runnable handler) { attack.setOnAction(event -> handler.run()); }
    public void setOnPickItem(Runnable handler) { pickItem.setOnAction(event -> handler.run()); }
    public void setOnLineMove(Runnable handler) { lineMove.setOnAction(event -> handler.run()); }
    public void setOnInteract(Runnable handler) { interact.setOnAction(event -> handler.run()); }
    public void setOnUseDoor(Runnable handler) { useDoor.setOnAction(event -> handler.run()); }
    public void setOnUseItem(Runnable handler) { useItem.setOnAction(event -> handler.run()); }
    public void setOnEquipWeapon(Runnable handler) { equipWeapon.setOnAction(event -> handler.run()); }
    public void setOnEquipArmor(Runnable handler) { equipArmor.setOnAction(event -> handler.run()); }
    public void setOnShop(Runnable handler) { shop.setOnAction(event -> handler.run()); }
    public void setOnRoulette(Runnable handler) { roulette.setOnAction(event -> handler.run()); }
    public void setOnSave(Runnable handler) { save.setOnAction(event -> handler.run()); }
    public void setOnLoad(Runnable handler) { load.setOnAction(event -> handler.run()); }
    public void setOnReplay(Runnable handler) { replay.setOnAction(event -> handler.run()); }

    public void refresh(Game game) {
        boolean finished = game.getState() != casinoescape.model.GameState.IN_PROGRESS;
        boolean lineMovementAvailable = !finished && game.canMovePlayerInLine();
        boolean shopAvailable = !finished && game.canUseAdjacentShop();
        boolean rouletteAvailable = !finished && game.canPlayAdjacentMinigame();
        lineMove.setText(lineMovementAvailable ? "Movimiento linea" : "Requiere Pastilla");
        endTurn.setDisable(finished);
        attack.setDisable(finished);
        pickItem.setDisable(finished);
        lineMove.setDisable(!lineMovementAvailable);
        interact.setDisable(finished);
        useDoor.setDisable(finished);
        useItem.setDisable(finished);
        equipWeapon.setDisable(finished);
        equipArmor.setDisable(finished);
        shop.setDisable(!shopAvailable);
        roulette.setDisable(!rouletteAvailable);
        replay.setVisible(finished);
        replay.setManaged(finished);
        replay.setDisable(!finished);
    }
}
