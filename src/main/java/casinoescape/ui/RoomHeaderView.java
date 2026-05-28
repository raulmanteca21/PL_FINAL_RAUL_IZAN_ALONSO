package casinoescape.ui;

import casinoescape.model.Room;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class RoomHeaderView {
    private final VBox root = new VBox(4);
    private final Label roomTitle = new Label();

    public RoomHeaderView() {
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 12; -fx-background-color: #6E1B1B; -fx-border-color: #D4AF37; -fx-border-width: 4;");
        roomTitle.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #F6D36B;");
        root.getChildren().add(roomTitle);
    }

    public Node getNode() {
        return root;
    }

    public void refresh(Room room) {
        roomTitle.setText("♠ ♥ ♦ ♣  SALA " + room.getId() + " - " + room.getName().toUpperCase() + "  ♣ ♦ ♥ ♠");
    }
}
