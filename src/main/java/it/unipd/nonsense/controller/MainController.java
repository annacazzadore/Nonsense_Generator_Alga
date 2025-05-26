package it.unipd.nonsense.controller;

import it.unipd.nonsense.utils.AnalysisResult;
import it.unipd.nonsense.utils.SentenceAnalyzer;
import com.google.cloud.language.v1.AnalyzeSyntaxResponse;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * The main controller class for the sentence analysis application.
 * Handles user interactions and coordinates between the UI and analysis components.
 */
public class MainController {

    // FXML injected UI components
    @FXML private TextField sentence;  // Input field for the sentence to analyze
    @FXML private CheckBox tree;       // Checkbox to toggle syntax tree visualization
    @FXML private Button analyze;      // Button to trigger analysis

    /**
     * Analyzes the entered sentence when the analyze button is clicked.
     * Performs several validation steps and coordinates the analysis workflow.
     */
    @FXML
    private void analyzeSentence() {
        String inputText = sentence.getText().trim();

        if (!inputText.isEmpty()) {
            try (SentenceAnalyzer analyzer = new SentenceAnalyzer()) {
                // Language validation
                if (!analyzer.isEnglish(inputText)) {
                    showError("Please avoid using non-English terms.");
                    return;
                }

                // Perform syntactic analysis
                AnalyzeSyntaxResponse syntaxResponse = analyzer.analyzeSentence(inputText);

                // Toxicity check
                float toxicityScore = analyzer.getToxicityScore(inputText);
                double toxicityPercentage = toxicityScore * 100;
                System.out.printf("Toxicity rate: %.2f%%%n", toxicityPercentage);


                if (toxicityScore >= 0.5f) {
                    showErrorDialog(toxicityScore);
                    return;
                }

                // Prepare the result view
                Stage currentStage = (Stage) analyze.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource(tree.isSelected() ? "/views/syntaxTree.fxml" : "/views/noSyntaxTree.fxml"));

                // Load the appropriate view
                Parent root = loader.load();
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root));
                newStage.setTitle("Sentence Analyzer");
                newStage.setResizable(false);

                // Package the analysis results
                AnalysisResult result = new AnalysisResult();
                result.setOriginalText(inputText);
                result.setSyntaxResponse(syntaxResponse);
                result.setWordsByType(analyzer.extractWordsByType(syntaxResponse));

                // Save any new words to dictionaries
                analyzer.saveWordsToDictionaries(result.getWordsByType());

                // Initialize the appropriate controller
                if (tree.isSelected()) {
                    SyntaxTreeController syntaxTreeController = loader.getController();
                    syntaxTreeController.initializeWithResult(result);
                } else {
                    NoSyntaxTreeController noSyntaxTreeController = loader.getController();
                    noSyntaxTreeController.initializeWithResult(result);
                }

                // Show the results window and close the current one
                newStage.show();
                currentStage.close();

            } catch (Exception e) {
                e.printStackTrace();
                showError("An error occurred while analyzing the sentence: " + e.getMessage());
            }
        } else {
            showError("Please enter a sentence to analyze.");
        }
    }

    /**
     * Shows a custom error dialog for toxic content detection.
     *
     * @param toxicityScore The detected toxicity score (0.0 to 1.0)
     */
    private void showErrorDialog(double toxicityScore) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/popUp.fxml"));
            Parent root = loader.load();

            // Configure the popup controller
            PopUpController controller = loader.getController();
            controller.setToxicityRate(toxicityScore);

            // Setup and show the popup window
            Stage popUpStage = new Stage();
            popUpStage.setResizable(false);
            popUpStage.setScene(new Scene(root));
            popUpStage.setX(370);  // Position on screen
            popUpStage.setY(265);
            popUpStage.setTitle("Error");
            popUpStage.initModality(Modality.APPLICATION_MODAL);
            popUpStage.initOwner(analyze.getScene().getWindow());
            popUpStage.showAndWait();

            // Clear the input field
            sentence.clear();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows a standard error alert dialog.
     *
     * @param message The error message to display
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Sets the text in the sentence input field.
     *
     * @param text The text to set (ignored if null or empty)
     */
    public void setSentenceText(String text) {
        if (text != null && !text.trim().isEmpty()) {
            sentence.setText(text.trim());
        }
    }
}