package casinoescape.ui;

import casinoescape.movement.ShortestPathInfo;
import casinoescape.structures.MyLinkedList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class RoutePanelView {
    private final VBox root = new VBox(6);
    private final Label route = new Label();
    private final Label roomDistance = new Label();
    private final Label nextRoom = new Label();
    private final Label cellDistance = new Label();

    public RoutePanelView() {
        root.setStyle("-fx-padding: 12; -fx-border-color: #0000ff; -fx-border-width: 3; -fx-background-color: #f4f8ff;");
        Label title = new Label("Mapa / Ruta");
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        route.setWrapText(true);
        root.getChildren().addAll(title, route, roomDistance, nextRoom, cellDistance);
    }

    public Node getNode() {
        return root;
    }

    public void refresh(ShortestPathInfo info) {
        route.setText("Ruta: " + formatPath(info.getRoomPath()));
        roomDistance.setText("Distancia salas: " + formatDistance(info.getRoomDistance()));
        nextRoom.setText("Siguiente sala: " + (info.getRecommendedNextRoomId() == ShortestPathInfo.NO_RECOMMENDED_ROOM
                ? "salida/actual" : String.valueOf(info.getRecommendedNextRoomId())));
        cellDistance.setText("Distancia puerta/salida: " + formatDistance(info.getCellDistance()));
    }

    private String formatPath(MyLinkedList<Integer> path) {
        if (path == null || path.isEmpty()) {
            return "sin ruta";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            if (i > 0) {
                builder.append(" -> ");
            }
            builder.append(path.get(i));
        }
        return builder.toString();
    }

    private String formatDistance(int distance) {
        return distance == ShortestPathInfo.NO_DISTANCE ? "sin ruta" : String.valueOf(distance);
    }
}
