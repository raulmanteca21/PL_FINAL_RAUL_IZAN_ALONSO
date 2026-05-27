package casinoescape.ui;

import casinoescape.game.Game;
import casinoescape.movement.ShortestPathInfo;
import casinoescape.structures.MyLinkedList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class RoutePanelView {
    private final VBox root = new VBox(6);
    private final Label currentRoom = new Label();
    private final Label connections = new Label();
    private final Label route = new Label();
    private final Label roomDistance = new Label();
    private final Label nextRoom = new Label();
    private final Label cellDistance = new Label();

    public RoutePanelView() {
        root.setStyle("-fx-padding: 12; -fx-border-color: #d4af37; -fx-border-width: 3; -fx-background-color: #f4fff8;");
        Label title = new Label("Plano del casino");
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #064f36;");
        route.setWrapText(true);
        connections.setWrapText(true);
        root.getChildren().addAll(title, currentRoom, connections, route, roomDistance, nextRoom, cellDistance);
    }

    public Node getNode() {
        return root;
    }

    public void refresh(Game game, ShortestPathInfo info) {
        currentRoom.setText("Sala actual: " + game.getCurrentRoom().getId() + " - " + game.getCurrentRoom().getName());
        connections.setText("Conexiones: " + formatPath(game.getMap().getConnectedRooms(game.getCurrentRoom().getId())));
        route.setText("Ruta recomendada: " + formatPath(info.getRoomPath()));
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
