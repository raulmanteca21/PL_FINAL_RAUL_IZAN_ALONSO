package casinoescape.ui;

import casinoescape.combat.CombatResult;
import casinoescape.game.Game;
import casinoescape.game.RouletteResult;
import casinoescape.items.Item;
import casinoescape.items.ShopItem;
import casinoescape.movement.Direction;
import casinoescape.model.Cell;
import casinoescape.model.CellType;
import casinoescape.model.GameState;
import casinoescape.model.Position;
import casinoescape.persistence.GameSaveLoader;
import casinoescape.persistence.GameSaveWriter;
import casinoescape.structures.MyLinkedList;
import java.nio.file.Path;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class GameController {
    private static final Path DEFAULT_SAVE_PATH = Path.of("saves", "savegame.json");

    private Game game;
    private final RoomGridView roomGridView = new RoomGridView();
    private final PlayerPanelView playerPanelView = new PlayerPanelView();
    private final InventoryPanelView inventoryPanelView = new InventoryPanelView();
    private final ActionPanelView actionPanelView = new ActionPanelView();
    private final LogPanelView logPanelView = new LogPanelView();
    private final RoutePanelView routePanelView = new RoutePanelView();
    private GameState lastAnnouncedEndState = GameState.IN_PROGRESS;

    public GameController(Game game) {
        if (game == null) {
            throw new IllegalArgumentException("Game is required");
        }
        this.game = game;
        connectEvents();
    }

    public Parent createView() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(14));
        root.setStyle("-fx-background-color: white;");

        VBox leftPanel = new VBox(12, playerPanelView.getNode(), inventoryPanelView.getNode());
        leftPanel.setPrefWidth(245);

        VBox rightPanel = new VBox(12, routePanelView.getNode(), logPanelView.getNode());
        rightPanel.setPrefWidth(300);

        root.setLeft(leftPanel);
        root.setCenter(roomGridView.getNode());
        root.setRight(rightPanel);
        root.setBottom(actionPanelView.getNode());
        return root;
    }

    public void refreshAll() {
        MyLinkedList<Position> reachableCells = game.getState() == GameState.IN_PROGRESS
                ? game.getReachableCells()
                : new MyLinkedList<>();
        roomGridView.refresh(game.getCurrentRoom(), reachableCells);
        playerPanelView.refresh(game);
        inventoryPanelView.refresh(game.getInventory());
        routePanelView.refresh(game.getShortestPathInfo());
        logPanelView.refresh(game.getLog());
        actionPanelView.refresh(game);
        checkEndState();
    }

    private void connectEvents() {
        roomGridView.setOnCellClicked(this::handleCellClick);
        actionPanelView.setOnEndTurn(this::handleEndTurn);
        actionPanelView.setOnAttack(this::handleAttack);
        actionPanelView.setOnPickItem(this::handlePickItem);
        actionPanelView.setOnLineMove(this::handleLineMove);
        actionPanelView.setOnInteract(this::handleInteract);
        actionPanelView.setOnUseDoor(this::handleUseDoor);
        actionPanelView.setOnUseItem(this::handleUseItem);
        actionPanelView.setOnEquipWeapon(this::handleEquipWeapon);
        actionPanelView.setOnEquipArmor(this::handleEquipArmor);
        actionPanelView.setOnShop(this::handleOpenShop);
        actionPanelView.setOnRoulette(this::handleRussianRoulette);
        actionPanelView.setOnSave(this::handleSave);
        actionPanelView.setOnLoad(this::handleLoad);
    }

    private void handleCellClick(Position position) {
        try {
            Cell cell = game.getCurrentRoom().getCell(position);
            if (cell.isInteractive()) {
                interactWith(position);
            } else {
                game.movePlayer(position);
            }
            refreshAll();
        } catch (RuntimeException exception) {
            showError(exception);
        }
    }

    private void handleEndTurn() {
        try {
            game.endTurn();
            refreshAll();
        } catch (RuntimeException exception) {
            showError(exception);
        }
    }

    private void handleAttack() {
        try {
            CombatResult result = game.attackAdjacentEnemy();
            refreshAll();
            String message = result.isDefenderDied()
                    ? "Enemigo derrotado. Dano: " + result.getDamageDealt()
                    : "Dano causado: " + result.getDamageDealt();
            showInfo("Combate", message);
        } catch (RuntimeException exception) {
            showError(exception);
        }
    }

    private void handlePickItem() {
        try {
            Item item = game.pickUpAdjacentItem();
            refreshAll();
            showInfo("Recoger", "Has recogido: " + item.getName());
        } catch (RuntimeException exception) {
            showError(exception);
        }
    }

    private void handleLineMove() {
        try {
            ChoiceDialog<String> dialog = new ChoiceDialog<>("UP", "UP", "DOWN", "LEFT", "RIGHT");
            dialog.setTitle("Movimiento en linea");
            dialog.setHeaderText("Elige una direccion ortogonal");
            Optional<String> selected = dialog.showAndWait();
            if (selected.isPresent()) {
                game.movePlayerInLine(Direction.valueOf(selected.get()));
                refreshAll();
            }
        } catch (RuntimeException exception) {
            showError(exception);
        }
    }

    private void handleInteract() {
        try {
            Position target = findCurrentOrAdjacentInteractive();
            if (target == null) {
                throw new IllegalStateException("No hay elemento interactivo adyacente");
            }
            interactWith(target);
            refreshAll();
        } catch (RuntimeException exception) {
            showError(exception);
        }
    }

    private void handleUseDoor() {
        try {
            Position doorPosition = findCurrentOrAdjacentCellOfType(CellType.DOOR);
            if (doorPosition == null) {
                throw new IllegalStateException("No hay puerta adyacente");
            }
            game.useDoorAt(doorPosition);
            refreshAll();
        } catch (RuntimeException exception) {
            showError(exception);
        }
    }

    private void handleUseItem() {
        Item item = requireSelectedItem();
        if (item == null) {
            return;
        }
        try {
            game.useItem(item.getId());
            refreshAll();
        } catch (RuntimeException exception) {
            showError(exception);
        }
    }

    private void handleEquipWeapon() {
        Item item = requireSelectedItem();
        if (item == null) {
            return;
        }
        try {
            game.equipWeapon(item.getId());
            refreshAll();
        } catch (RuntimeException exception) {
            showError(exception);
        }
    }

    private void handleEquipArmor() {
        Item item = requireSelectedItem();
        if (item == null) {
            return;
        }
        try {
            game.equipArmor(item.getId());
            refreshAll();
        } catch (RuntimeException exception) {
            showError(exception);
        }
    }

    private void handleOpenShop() {
        try {
            ChoiceDialog<String> dialog = new ChoiceDialog<>();
            dialog.setTitle("Bar / Tienda");
            dialog.setHeaderText("Compra con fichas de casino");
            for (int i = 0; i < game.getBarShop().size(); i++) {
                ShopItem item = game.getBarShop().getItem(i);
                dialog.getItems().add(item.getId() + " - " + item.getName() + " (" + item.getPrice() + " fichas)");
            }
            if (!dialog.getItems().isEmpty()) {
                dialog.setSelectedItem(dialog.getItems().get(0));
            }
            Optional<String> selected = dialog.showAndWait();
            if (selected.isPresent()) {
                String shopItemId = selected.get().split(" - ")[0];
                game.buyFromAdjacentBar(shopItemId);
                refreshAll();
            }
        } catch (RuntimeException exception) {
            showError(exception);
        }
    }

    private void handleRussianRoulette() {
        try {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Ruleta rusa");
            confirmation.setHeaderText("Minijuego opcional de alto riesgo");
            confirmation.setContentText("Puedes rechazarlo. Si aceptas, puede darte fichas o causarte derrota inmediata.");
            Optional<ButtonType> result = confirmation.showAndWait();
            boolean accepts = result.isPresent() && result.get() == ButtonType.OK;
            RouletteResult rouletteResult = game.playRussianRoulette(accepts);
            refreshAll();
            showInfo("Ruleta rusa", rouletteResult.getMessage());
        } catch (RuntimeException exception) {
            showError(exception);
        }
    }

    private void handleSave() {
        try {
            new GameSaveWriter().save(game, DEFAULT_SAVE_PATH);
            showInfo("Guardar", "Partida guardada en " + DEFAULT_SAVE_PATH);
            refreshAll();
        } catch (RuntimeException exception) {
            showError(exception);
        }
    }

    private void handleLoad() {
        try {
            game = new GameSaveLoader().load(DEFAULT_SAVE_PATH);
            lastAnnouncedEndState = GameState.IN_PROGRESS;
            showInfo("Cargar", "Partida cargada desde " + DEFAULT_SAVE_PATH);
            refreshAll();
        } catch (RuntimeException exception) {
            showError(exception);
        }
    }

    private void interactWith(Position target) {
        Cell cell = game.getCurrentRoom().getCell(target);
        if (cell.getType() == CellType.SHOP) {
            handleOpenShop();
        } else if (cell.getType() == CellType.MINIGAME) {
            handleRussianRoulette();
        } else {
            showInfo("Interaccion", game.interactSimpleAt(target));
        }
    }

    private Position findCurrentOrAdjacentInteractive() {
        return game.findCurrentOrAdjacentInteractive();
    }

    private Position findCurrentOrAdjacentCellOfType(CellType type) {
        return game.findCurrentOrAdjacentCellOfType(type);
    }

    private Item requireSelectedItem() {
        Item item = inventoryPanelView.getSelectedItem();
        if (item == null) {
            showInfo("Inventario", "Selecciona primero un objeto del inventario.");
        }
        return item;
    }

    private void checkEndState() {
        if (game.getState() == GameState.IN_PROGRESS || game.getState() == lastAnnouncedEndState) {
            return;
        }
        lastAnnouncedEndState = game.getState();
        if (game.getState() == GameState.VICTORY) {
            showInfo("Victoria", Game.VICTORY_MESSAGE);
        } else if (game.getState() == GameState.DEFEAT) {
            showInfo("Derrota", "La partida ha terminado en derrota.");
        }
    }

    private void showError(RuntimeException exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Accion no valida");
        alert.setHeaderText("No se pudo completar la accion");
        alert.setContentText(exception.getMessage());
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
