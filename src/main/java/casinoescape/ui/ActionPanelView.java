package casinoescape.ui;

import casinoescape.game.Game;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;

public class ActionPanelView {
    private static final String DISABLED_STYLE = "-fx-background-color: #333333; -fx-text-fill: #8A8A8A; -fx-font-weight: bold; -fx-border-color: #555555; -fx-border-width: 1; -fx-padding: 6 8 6 8;";

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
        root.setStyle("-fx-padding: 10; -fx-background-color: #141414; -fx-border-color: #D4AF37; -fx-border-width: 2 0 0 0;");
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
        replay.setStyle(style("#F6D36B", "#5A1717", "#9E1B1B", 2));
        replay.setVisible(false);
        replay.setManaged(false);
        root.getChildren().addAll(endTurn, attack, pickItem, lineMove, interact, useDoor, useItem, equipWeapon, equipArmor, shop, roulette, save, load, replay);
    }

    private void styleActionButtons() {
        endTurn.setStyle(style("#1F7A3A", "#FFF4D6", "#D4AF37", 1));
        attack.setStyle(style("#9E1B1B", "#FFF4D6", "#D4AF37", 1));
        pickItem.setStyle(style("#FFF4D6", "#17130A", "#D4AF37", 1));
        lineMove.setStyle(style("#4FD06B", "#111111", "#D4AF37", 1));
        interact.setStyle(style("#FFF4D6", "#17130A", "#D4AF37", 1));
        useDoor.setStyle(style("#2F6F9F", "#FFF4D6", "#D4AF37", 1));
        useItem.setStyle(style("#FFF4D6", "#17130A", "#D4AF37", 1));
        equipWeapon.setStyle(style("#FFF4D6", "#17130A", "#D4AF37", 1));
        equipArmor.setStyle(style("#FFF4D6", "#17130A", "#D4AF37", 1));
        shop.setStyle(style("#F2C94C", "#111111", "#D4AF37", 1));
        roulette.setStyle(style("#6C4AB6", "#FFF4D6", "#D4AF37", 1));
        save.setStyle(style("#D4AF37", "#111111", "#FFF4D6", 1));
        load.setStyle(style("#B88A2A", "#111111", "#FFF4D6", 1));
    }

    private String style(String background, String text, String border, int borderWidth) {
        return "-fx-background-color: " + background + ";"
                + "-fx-text-fill: " + text + ";"
                + "-fx-font-weight: bold;"
                + "-fx-border-color: " + border + ";"
                + "-fx-border-width: " + borderWidth + ";"
                + "-fx-padding: 6 8 6 8;";
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
        styleActionButtons();
        if (!lineMovementAvailable) {
            lineMove.setStyle(DISABLED_STYLE);
        }
        if (!shopAvailable) {
            shop.setStyle(DISABLED_STYLE);
        }
        if (!rouletteAvailable) {
            roulette.setStyle(DISABLED_STYLE);
        }
        if (finished) {
            endTurn.setStyle(DISABLED_STYLE);
            attack.setStyle(DISABLED_STYLE);
            pickItem.setStyle(DISABLED_STYLE);
            interact.setStyle(DISABLED_STYLE);
            useDoor.setStyle(DISABLED_STYLE);
            useItem.setStyle(DISABLED_STYLE);
            equipWeapon.setStyle(DISABLED_STYLE);
            equipArmor.setStyle(DISABLED_STYLE);
        }
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
