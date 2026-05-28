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
            String prefix = cell.getDoor().isLocked() ? "L" : "P";
            return prefix + "->" + cell.getDoor().getDestinationRoomId();
        }
        if (cell.getType() == CellType.EXIT) {
            return "OUT";
        }
        if (cell.getType() == CellType.SHOP) {
            return "BAR";
        }
        if (cell.getType() == CellType.MINIGAME) {
            return "RUL";
        }
        return switch (cell.getType()) {
            case EMPTY -> ".";
            case OBSTACLE -> "#";
            case PLAYER -> "J";
            case ENEMY -> "E";
            case ITEM -> "OBJ";
            case DOOR -> "P";
            case NPC -> "NPC";
            case TRAP -> "TRAP";
            case SHOP -> "BAR";
            case EXIT -> "OUT";
            case MINIGAME -> "RUL";
        };
    }

    private String styleFor(CellType type, boolean reachable) {
        String background = switch (type) {
            case EMPTY -> reachable ? "#4FD06B" : "#0F4A36";
            case OBSTACLE -> "#1B1B1B";
            case PLAYER -> "#D4AF37";
            case ENEMY -> "#9E1B1B";
            case ITEM -> "#F2C94C";
            case DOOR -> "#2F6F9F";
            case NPC -> "#B66A2C";
            case TRAP -> "#C0392B";
            case SHOP -> "#E0B84F";
            case EXIT -> "#2E8B57";
            case MINIGAME -> "#6C4AB6";
        };
        String text = switch (type) {
            case EMPTY -> reachable ? "#111111" : "#FFF4D6";
            case PLAYER, ITEM, SHOP -> "#111111";
            default -> "#FFF4D6";
        };
        String border = switch (type) {
            case PLAYER -> "#F6D36B";
            case ENEMY -> "#4A0D0D";
            case DOOR -> "#D4AF37";
            default -> "#06281F";
        };
        int borderWidth = type == CellType.PLAYER || type == CellType.DOOR ? 3 : 2;
        return "-fx-background-color: " + background + ";"
                + "-fx-text-fill: " + text + ";"
                + "-fx-border-color: " + border + ";"
                + "-fx-border-width: " + borderWidth + ";"
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
