package it.unipd.nonsense;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private static final String FXML_PATH = "/views/Main.fxml";
    private static final String APP_TITLE = "Random Sentence Generator ALGA";
    private static final int MIN_WIDTH = 800;
    private static final int MIN_HEIGHT = 600;

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setTitle(APP_TITLE);
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(MIN_WIDTH);
            primaryStage.setMinHeight(MIN_HEIGHT);
            primaryStage.sizeToScene();
            primaryStage.setResizable(true);

            primaryStage.show();
        } catch (Exception e) {
            handleLaunchException(e);
        }
    }

    private void handleLaunchException(Exception e) {
        System.err.println("Failed to launch application: " + e.getMessage());
        e.printStackTrace();
        System.exit(1);
    }

    public static void main(String[] args) {
        launch(args);
    }
}