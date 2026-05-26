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
        if (cell.getType() == CellType.EXIT) {
            return "OUT";
        }
        if (cell.getType() == CellType.SHOP) {
            return "$";
        }
        if (cell.getType() == CellType.MINIGAME) {
            return "?";
        }
        return switch (cell.getType()) {
            case EMPTY -> ".";
            case OBSTACLE -> "#";
            case PLAYER -> "P";
            case ENEMY -> "E";
            case ITEM -> "O";
            case DOOR -> "D";
            case NPC -> "N";
            case TRAP -> "!";
            case SHOP -> "$";
            case EXIT -> "OUT";
            case MINIGAME -> "?";
        };
    }

    private String styleFor(CellType type, boolean reachable) {
        String background = switch (type) {
            case EMPTY -> reachable ? "#b7f7c3" : "#ffffff";
            case OBSTACLE -> "#4f4f4f";
            case PLAYER -> "#ffb347";
            case ENEMY -> "#c0392b";
            case ITEM -> "#f1c40f";
            case DOOR -> "#6fa8dc";
            case NPC -> "#f6b26b";
            case TRAP -> "#e06666";
            case SHOP -> "#ffe599";
            case EXIT -> "#93c47d";
            case MINIGAME -> "#b4a7d6";
        };
        return "-fx-background-color: " + background + ";"
                + "-fx-border-color: #008f4c;"
                + "-fx-border-width: 2;"
                + "-fx-font-size: 18;"
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
