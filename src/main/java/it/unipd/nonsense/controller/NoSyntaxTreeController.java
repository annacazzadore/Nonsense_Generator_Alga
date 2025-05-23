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

public class NoSyntaxTreeController {
    @FXML private Spinner<Integer> number;
    @FXML private ChoiceBox<String> templates;
    @FXML private Label wordsLabel;
    @FXML private Button generate;
    private AnalysisResult currentResult;
    private String selectedTemplate = "-";

    private int numberValue = 1; // valore di default

    public void initializeWithResult(AnalysisResult result) {
        // Salva il risultato come campo della classe
        this.currentResult = result;

        // Prepara il testo da mostrare nel TextField
        StringJoiner wordsJoiner = new StringJoiner(", ");
        Map<String, Set<String>> wordsByType = result.getWordsByType();

        // Aggiungi nomi, verbi e aggettivi al testo
        if (wordsByType.containsKey("NOUN")) {
            wordsByType.get("NOUN").forEach(word -> wordsJoiner.add(word));
        }
        if (wordsByType.containsKey("VERB")) {
            wordsByType.get("VERB").forEach(word -> wordsJoiner.add(word));
        }
        if (wordsByType.containsKey("ADJ")) {
            wordsByType.get("ADJ").forEach(word -> wordsJoiner.add(word));
        }

        // Imposta il testo nel TextField
        wordsLabel.setText(wordsJoiner.toString());
    }

    @FXML
    private void handleBack(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main.fxml"));
            Parent root = loader.load();

            // Ottieni il controller e passa la frase
            MainController mainController = loader.getController();
            mainController.setSentenceText(currentResult.getOriginalText());

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.setTitle("Random Sentence Generator ALGA");
            stage.show();

            // Chiudi la finestra attuale
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGenerate(MouseEvent event) {
        try {
            // Ottieni i parametri dall'interfaccia
            int numSentences;
            String template;

            try {
                numSentences = number.getValue();
                if (numSentences <= 0) {
                    showAlert("Error", "The number of sentences must be greater than 0.");
                    return;
                }

                template = templates.getValue().toString();
                if (template == null || template.isEmpty() || template.equals("-")) {
                    showAlert("Error", "Select a valid template.");
                    return;
                }

                // Salva il template selezionato
                this.selectedTemplate = template;

            } catch (NumberFormatException e) {
                showAlert("Error", "Insert a valid number.");
                return;
            }

            // Estrai le parole dalle liste
            Map<String, Set<String>> wordsByType = currentResult.getWordsByType();
            List<String> nouns = new ArrayList<>(wordsByType.getOrDefault("NOUN", Collections.emptySet()));
            List<String> verbs = new ArrayList<>(wordsByType.getOrDefault("VERB", Collections.emptySet()));
            List<String> adjectives = new ArrayList<>(wordsByType.getOrDefault("ADJ", Collections.emptySet()));

            // Genera le frasi
            SentenceGenerator generator = new SentenceGenerator();
            List<String> sentences = generator.generateSentences(numSentences, template, nouns, verbs, adjectives);

            // Salva il numero di frasi
            numberValue = numSentences;

            // Mostra i risultati
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/results.fxml"));
            Parent root = loader.load();

            // Ottieni il controller dei risultati
            ResultsController resultsController = loader.getController();
            resultsController.setPreviousView("noTree");
            resultsController.setAnalysisResult(currentResult);
            resultsController.showGeneratedSentences(sentences);
            resultsController.setNumberValue(numberValue);
            resultsController.setSelectedTemplate(selectedTemplate); // Passa il template selezionato

            // Mostra la nuova finestra
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.setTitle("Results");
            stage.show();

            // Chiudi la finestra corrente
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            showAlert("Error", "An error occurred while generating the sentences: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            showAlert("Error", e.getMessage());
        }
    }

    //gestione del menú per i template
    @FXML
    public void initialize() {
        try {
            SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, numberValue); // min, max, initial
            number.setValueFactory(valueFactory);
            // 1. Leggi tutte le righe dal file
            List<String> options = Files.readAllLines(Paths.get("src/resources/dictionaries/templates.txt"));

            // 2. Pulisci eventuali elementi esistenti e aggiungi i nuovi
            templates.getItems().clear();
            templates.getItems().addAll(options);

            // 3. Imposta un valore predefinito (opzionale)
            if (!options.isEmpty()) {
                templates.setValue(selectedTemplate != null ? selectedTemplate : "-");
            }

        } catch (IOException e) {
            System.err.println("Failed to load options." + e.getMessage());

            // Fallback con valori di default
            templates.setValue("-");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

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