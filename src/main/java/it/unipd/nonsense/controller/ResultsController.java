package it.unipd.nonsense.controller;

import it.unipd.nonsense.utils.AnalysisResult;
import it.unipd.nonsense.utils.SentenceAnalyzer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for the results view that displays generated sentences and their toxicity analysis.
 * Handles sentence display, toxicity checking, file saving, and navigation back to previous views.
 */
public class ResultsController {
    private static final Logger LOGGER = Logger.getLogger(ResultsController.class.getName());

    /**
     * Default filename for saving non-toxic sentences
     */
    private static final String GENERATED_SENTENCES_FILE = "generated_sentences.txt";

    /**
     * Toxicity threshold (0.5 = 50%) above which sentences are considered toxic
     */
    private static final float TOXICITY_THRESHOLD = 0.5f;

    // FXML-injected UI components
    @FXML private Button end;          // Button to exit the application
    @FXML private ImageView back;      // Button to navigate back
    @FXML private TextArea resultArea; // Text area to display results

    // Controller state
    private String previousView;       // Type of previous view ("tree" or "noTree")
    private AnalysisResult analysisResult; // Analysis results from previous view
    private int numberValue;           // Number of sentences requested
    private String selectedTemplate;   // Template used for generation

    /**
     * Executor service for background sentence processing
     */
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    /**
     * Sets the previous view type for back navigation.
     * @param previousView The view type ("tree" or "noTree")
     * @throws NullPointerException if previousView is null
     */
    public void setPreviousView(String previousView) {
        this.previousView = Objects.requireNonNull(previousView, "Previous view cannot be null");
    }

    /**
     * Sets the analysis results from sentence generation.
     * @param analysisResult The analysis results containing linguistic data
     * @throws NullPointerException if analysisResult is null
     */
    public void setAnalysisResult(AnalysisResult analysisResult) {
        this.analysisResult = Objects.requireNonNull(analysisResult, "Analysis result cannot be null");
    }

    /**
     * Sets the template used for sentence generation.
     * @param template The template string
     */
    public void setSelectedTemplate(String template) {
        this.selectedTemplate = template;
    }

    /**
     * Sets the number of sentences that were requested.
     * @param value The number of sentences
     */
    public void setNumberValue(int value) {
        this.numberValue = value;
    }

    /**
     * Handles application exit when the end button is clicked.
     * Closes the current window and terminates the JavaFX platform.
     */
    @FXML
    private void handleEnd() {
        Stage stage = (Stage) end.getScene().getWindow();
        stage.close();
        Platform.exit();
    }

    /**
     * Handles navigation back to the previous view.
     * Loads the appropriate FXML based on previous view type.
     */
    @FXML
    private void handleBack() {
        try {
            // Determine which view to return to
            String fxmlFile = "tree".equals(previousView)
                    ? "/it/unipd/nonsense/views/syntaxTree.fxml"
                    : "/it/unipd/nonsense/views/noSyntaxTree.fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Configure and show the previous view
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.setTitle("Sentence Analyzer");
            stage.show();

            // Initialize the previous controller with current state
            initializePreviousController(loader);

            // Close current results window
            Stage currentStage = (Stage) back.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error while navigating back", e);
            showErrorAlert("Navigation Error", "Unable to load the previous view");
        }
    }

    /**
     * Initializes the previous controller with current analysis data.
     * @param loader The FXMLLoader containing the controller to initialize
     */
    private void initializePreviousController(FXMLLoader loader) {
        if ("tree".equals(previousView)) {
            SyntaxTreeController controller = loader.getController();
            controller.initializeWithResult(analysisResult);
            controller.setNumberValue(numberValue);
            controller.setSelectedTemplate(selectedTemplate);
        } else {
            NoSyntaxTreeController controller = loader.getController();
            controller.initializeWithResult(analysisResult);
            controller.setNumberValue(numberValue);
            controller.setSelectedTemplate(selectedTemplate);
        }
    }

    /**
     * Saves sentences to a text file with numbered formatting.
     * @param sentences The sentences to save
     * @param filePath The destination file path
     */
    public void saveSentencesToFile(List<String> sentences, String filePath) {
        Path path = Paths.get(filePath);

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            // Write each sentence with line number
            for (int i = 0; i < sentences.size(); i++) {
                writer.write(String.format("%d. %s%n%n", i + 1, sentences.get(i)));
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error while saving sentences to file", e);
            showErrorAlert("Save Error", "Unable to save sentences to file");
        }
    }

    /**
     * Displays generated sentences with toxicity analysis in the text area.
     * Processes sentences in background thread and saves non-toxic ones to file.
     * @param inputSentences The sentences to display and analyze
     * @throws NullPointerException if inputSentences is null
     */
    public void showGeneratedSentences(List<String> inputSentences) {
        Objects.requireNonNull(inputSentences, "Input sentences cannot be null");

        // Clear previous results and reset executor
        resultArea.clear();
        executorService.shutdownNow();
        executorService = Executors.newSingleThreadScheduledExecutor();

        // Process sentences in background
        executorService.execute(() -> {
            List<String> allSentences = new ArrayList<>();
            List<String> nonToxicSentences = new ArrayList<>();

            try (SentenceAnalyzer analyzer = new SentenceAnalyzer()) {
                int sentenceIndex = 1;

                // Analyze each sentence
                for (String sentence : inputSentences) {
                    // Get toxicity score
                    float toxicityScore = analyzer.getToxicityScore(sentence);

                    // Format result text
                    String resultText = String.format("%d. %s\nToxicity: %.2f%%\n\n", sentenceIndex, sentence, toxicityScore * 100);

                    // Update UI on JavaFX thread
                    Platform.runLater(() -> resultArea.appendText(resultText));

                    allSentences.add(sentence);
                    sentenceIndex++;

                    // Stop if we've reached requested number
                    if (allSentences.size() >= numberValue) {
                        break;
                    }

                    // Save non-toxic sentences
                    if (toxicityScore < TOXICITY_THRESHOLD) {
                        nonToxicSentences.add(sentence);
                    }
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error analyzing sentence toxicity", e);
            }

            // Save filtered sentences to file
            saveSentencesToFile(nonToxicSentences, GENERATED_SENTENCES_FILE);
            executorService.shutdown();
        });
    }

    /**
     * Logs an error message (currently does not show user alert)
     * @param title The error title
     * @param message The error message
     */
    private void showErrorAlert(String title, String message) {
        LOGGER.warning(title + ": " + message);
    }
}