package casinoescape.ui;

import casinoescape.model.Cell;
import casinoescape.model.CellType;
import casinoescape.model.Position;
import casinoescape.model.Room;
import casinoescape.structures.MyLinkedList;
import java.util.function.Consumer;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class RoomGridView {
    private static final int CELL_SIZE = 82;

    private final GridPane grid = new GridPane();
    private Consumer<Position> onCellClicked;

    public RoomGridView() {
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(3);
        grid.setVgap(3);
        GridPane.setHgrow(grid, Priority.ALWAYS);
        GridPane.setVgrow(grid, Priority.ALWAYS);
    }

    public Node getNode() {
        return grid;
    }

    public void setOnCellClicked(Consumer<Position> onCellClicked) {
        this.onCellClicked = onCellClicked;
    }

    public void refresh(Room room, MyLinkedList<Position> reachableCells) {
        grid.getChildren().clear();
        for (int row = 0; row < room.getRows(); row++) {
            for (int column = 0; column < room.getColumns(); column++) {
                Position position = new Position(row, column);
                Button cellButton = createCellButton(room.getCell(position), isReachable(position, reachableCells));
                cellButton.setOnAction(event -> notifyCellClicked(position));
                grid.add(cellButton, column, row);
            }
        }
    }

    private Button createCellButton(Cell cell, boolean reachable) {
        Button button = new Button(symbolFor(cell));
        button.setMinSize(CELL_SIZE, CELL_SIZE);
        button.setPrefSize(CELL_SIZE, CELL_SIZE);
        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        button.setStyle(styleFor(cell.getType(), reachable));
        return button;
    }

    private String symbolFor(Cell cell) {
        if (cell.getDoor() != null) {
            String prefix = cell.getDoor().isLocked() ? "LOCK" : "P";
            return prefix + "->" + cell.getDoor().getDestinationRoomId();
        }
        if (cell.getType() == CellType.EXIT) {
            return "SALIDA";
        }
        if (cell.getType() == CellType.SHOP) {
            return "BAR";
        }
        if (cell.getType() == CellType.MINIGAME) {
            return "RULETA";
        }
        return switch (cell.getType()) {
            case EMPTY -> ".";
            case OBSTACLE -> "#";
            case PLAYER -> "J";
            case ENEMY -> "E";
            case ITEM -> "OBJ";
            case DOOR -> "P";
            case NPC -> "N";
            case TRAP -> "TRAP";
            case SHOP -> "BAR";
            case EXIT -> "SALIDA";
            case MINIGAME -> "RULETA";
        };
    }

    private String styleFor(CellType type, boolean reachable) {
        String background = switch (type) {
            case EMPTY -> reachable ? "#7bd88f" : "#f7fff7";
            case OBSTACLE -> "#2f2f2f";
            case PLAYER -> "#d4af37";
            case ENEMY -> "#b51f1f";
            case ITEM -> "#f1c232";
            case DOOR -> "#3d85c6";
            case NPC -> "#d98b2b";
            case TRAP -> "#cc4125";
            case SHOP -> "#ffd966";
            case EXIT -> "#6aa84f";
            case MINIGAME -> "#8e7cc3";
        };
        return "-fx-background-color: " + background + ";"
                + "-fx-border-color: #0b2f24;"
                + "-fx-border-width: 2;"
                + "-fx-font-size: 13;"
                + "-fx-font-weight: bold;";
    }

    private boolean isReachable(Position position, MyLinkedList<Position> reachableCells) {
        return reachableCells != null && reachableCells.contains(position);
    }

    private void notifyCellClicked(Position position) {
        if (onCellClicked != null) {
            onCellClicked.accept(position);
        }
    }
}
