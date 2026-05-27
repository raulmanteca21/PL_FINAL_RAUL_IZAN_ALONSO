package casinoescape.ui;

import casinoescape.game.Game;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CasinoEscapeApp extends Application {
    private static final int DEFAULT_TURNS = 125;
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 800;

    @Override
    public void start(Stage stage) {
        Game game = Game.createNewGame(DEFAULT_TURNS);
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
