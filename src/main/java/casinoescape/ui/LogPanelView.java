package casinoescape.ui;

import casinoescape.logging.GameLog;
import casinoescape.logging.LogEntry;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class LogPanelView {
    private final VBox root = new VBox(6);
    private final TextArea logArea = new TextArea();

    public LogPanelView() {
        root.setStyle("-fx-padding: 12; -fx-border-color: #d4af37; -fx-border-width: 3; -fx-background-color: #fff8df;");
        Label title = new Label("Registro de la mesa");
        title.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #5c2300;");
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setPrefHeight(430);
        logArea.setStyle("-fx-control-inner-background: #fffdf2; -fx-text-fill: #141414; -fx-border-color: #d4af37; -fx-border-width: 1;");
        root.getChildren().addAll(title, logArea);
    }

    public Node getNode() {
        return root;
    }

    public void refresh(GameLog log) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < log.size(); i++) {
            LogEntry entry = log.getEntry(i);
            if (entry.hasTurn()) {
                builder.append("[").append(entry.getTurn()).append("] ");
            }
            builder.append(entry.getMessage()).append(System.lineSeparator());
        }
        logArea.setText(builder.toString());
        logArea.positionCaret(logArea.getText().length());
    }
}
