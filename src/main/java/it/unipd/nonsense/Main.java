package it.unipd.nonsense;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The main entry point for the Random Sentence Generator ALGA application.
 * This class extends JavaFX's Application class and sets up the primary stage.
 */
public class Main extends Application {
    // Path to the FXML file that defines the UI layout
    private static final String FXML_PATH = "/views/Main.fxml";
    // Title to be displayed in the application window
    private static final String APP_TITLE = "Random Sentence Generator ALGA";
    // Minimum width of the application window
    private static final int MIN_WIDTH = 800;
    // Minimum height of the application window
    private static final int MIN_HEIGHT = 600;

    /**
     * The main entry point for JavaFX applications.
     *
     * @param primaryStage The primary stage for this application, where the scene is set.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML file that defines the user interface
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH));
            Parent root = loader.load();

            // Create a scene with the loaded UI hierarchy
            Scene scene = new Scene(root);

            // Configure the primary stage (main window)
            primaryStage.setTitle(APP_TITLE);
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(MIN_WIDTH);
            primaryStage.setMinHeight(MIN_HEIGHT);
            primaryStage.sizeToScene(); // Size the stage to fit the scene
            primaryStage.setResizable(true); // Allow window resizing

            // Display the primary stage
            primaryStage.show();
        } catch (Exception e) {
            // Handle any exceptions that occur during application launch
            handleLaunchException(e);
        }
    }

    /**
     * Handles exceptions that occur during application launch.
     * Prints error information to stderr and terminates the application.
     *
     * @param e The exception that occurred
     */
    private void handleLaunchException(Exception e) {
        System.err.println("Failed to launch application: " + e.getMessage());
        e.printStackTrace();
        System.exit(1); // Exit with error status
    }

    /**
     * The main method that launches the JavaFX application.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        launch(args); // Launch the JavaFX application
    }
}