package casinoescape.ui;

import casinoescape.game.Game;
import casinoescape.persistence.GameConfigLoader;
import java.nio.file.Path;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CasinoEscapeApp extends Application {
    private static final Path CONFIG_PATH = Path.of("config", "game_config.json");
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 800;

    @Override
    public void start(Stage stage) {
        Game game = new GameConfigLoader().load(CONFIG_PATH);
        GameController controller = new GameController(game);
        Scene scene = new Scene(controller.createView(), WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.setTitle("Casino Escape");
        stage.setScene(scene);
        stage.show();
        controller.refreshAll();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
