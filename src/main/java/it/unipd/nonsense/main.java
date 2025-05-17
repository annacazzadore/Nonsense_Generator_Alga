package it.unipd.nonsense;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class main extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        Parent root = FXMLLoader.load(getClass().getResource("/views/Main.fxml"));
        stage.setTitle("Finestra 1");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static void cambiaScene(String fxml) throws Exception {
        Parent pane = FXMLLoader.load(main.class.getResource(fxml));
        primaryStage.setScene(new Scene(pane));
    }

    public static void main(String[] args) {
        launch(args);
    }
}