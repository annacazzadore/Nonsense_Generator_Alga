package it.unipd.nonsense.controller;


import it.unipd.nonsense.utils.AnalysisResult;
import it.unipd.nonsense.utils.SentenceGenerator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public abstract class SyntaxController {

    @FXML protected Spinner<Integer> number;
    @FXML protected ChoiceBox<String> templates;
    @FXML protected Label wordsLabel;
    protected AnalysisResult currentResult;
    protected int numberValue = 1;
    protected String selectedTemplate = "-";

    @FXML
    public void initialize() {
        initializeTemplate(); // Inizializza Spinner e ChoiceBox
    }

    // Metodo per inizializzare i template (comune a entrambi i controller)
    @FXML
    public void initializeTemplate() {
        try {
            SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, numberValue);
            number.setValueFactory(valueFactory);

            List<String> options = Files.readAllLines(Paths.get("src/resources/dictionaries/templates.txt"));
            templates.getItems().clear();
            templates.getItems().addAll(options);

            if (!options.isEmpty()) {
                templates.setValue(selectedTemplate != null ? selectedTemplate : "-");
            }
        } catch (IOException e) {
            System.err.println("Failed to load options." + e.getMessage());
            templates.setValue("-");
        }
    }

    // Metodo per mostrare un alert (comune a entrambi i controller)
    protected void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Metodo per tornare alla schermata principale (comune a entrambi i controller)
    @FXML
    protected void handleBack(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main.fxml"));
            Parent root = loader.load();

            MainController mainController = loader.getController();
            mainController.setSentenceText(currentResult.getOriginalText());

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.setTitle("Random Sentence Generator ALGA");
            stage.show();

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Metodo per inizializzare con il risultato (parzialmente comune)
    public void initializeWithResult(AnalysisResult result) {
        this.currentResult = result;

        StringJoiner wordsJoiner = new StringJoiner(", ");
        Map<String, Set<String>> wordsByType = result.getWordsByType();

        if (wordsByType.containsKey("NOUN")) {
            wordsByType.get("NOUN").forEach(word -> wordsJoiner.add(word));
        }
        if (wordsByType.containsKey("VERB")) {
            wordsByType.get("VERB").forEach(word -> wordsJoiner.add(word));
        }
        if (wordsByType.containsKey("ADJ")) {
            wordsByType.get("ADJ").forEach(word -> wordsJoiner.add(word));
        }

        wordsLabel.setText(wordsJoiner.toString());
    }

    // Metodo per generare frasi (comune a entrambi i controller)
    @FXML
    protected void handleGenerate(MouseEvent event) {
        try {
            int numSentences = number.getValue();
            if (numSentences <= 0) {
                showAlert("Error", "The number of sentences must be greater than 0.");
                return;
            }

            String template = templates.getValue().toString();
            if (template == null || template.isEmpty() || template.equals("-")) {
                showAlert("Error", "Select a valid template.");
                return;
            }

            this.selectedTemplate = template;

            Map<String, Set<String>> wordsByType = currentResult.getWordsByType();
            List<String> nouns = new ArrayList<>(wordsByType.getOrDefault("NOUN", Collections.emptySet()));
            List<String> verbs = new ArrayList<>(wordsByType.getOrDefault("VERB", Collections.emptySet()));
            List<String> adjectives = new ArrayList<>(wordsByType.getOrDefault("ADJ", Collections.emptySet()));

            SentenceGenerator generator = new SentenceGenerator();
            List<String> sentences = generator.generateSentences(numSentences, template, nouns, verbs, adjectives);

            numberValue = numSentences;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/results.fxml"));
            Parent root = loader.load();

            ResultsController resultsController = loader.getController();
            resultsController.setPreviousView(getPreviousViewType());
            resultsController.setAnalysisResult(currentResult);
            resultsController.showGeneratedSentences(sentences);
            resultsController.setNumberValue(numberValue);
            resultsController.setSelectedTemplate(selectedTemplate);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.setTitle("Results");
            stage.show();

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            showAlert("Error", "An error occurred while generating the sentences: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            showAlert("Error", e.getMessage());
        }
    }

    // Metodo astratto per ottenere il tipo di vista precedente (da implementare nelle classi figlie)
    protected abstract String getPreviousViewType();

    // Metodi per settare i valori (comuni a entrambi i controller)
    public void setNumberValue(int value) {
        this.numberValue = value;
        if (number != null && number.getValueFactory() != null) {
            number.getValueFactory().setValue(value);
        }
    }

    public void setSelectedTemplate(String template) {
        this.selectedTemplate = template;
        if (templates != null) {
            templates.setValue(template);
        }
    }
}
