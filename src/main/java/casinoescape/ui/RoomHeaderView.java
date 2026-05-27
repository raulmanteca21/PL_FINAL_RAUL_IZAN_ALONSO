package casinoescape.ui;

import casinoescape.model.Room;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class RoomHeaderView {
    private final VBox root = new VBox(4);
    private final Label roomTitle = new Label();

    public RoomHeaderView() {
        root.setStyle("-fx-padding: 10; -fx-background-color: #fff8df; -fx-border-color: #d4af37; -fx-border-width: 3;");
        roomTitle.setStyle("-fx-font-size: 22; -fx-font-weight: bold; -fx-text-fill: #5c2300;");
        root.getChildren().add(roomTitle);
    }

    public Node getNode() {
        return root;
    }

    public void refresh(Room room) {
        roomTitle.setText("Sala " + room.getId() + " - " + room.getName());
    }
}
