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
    @FXML private TextField number;
    @FXML private ChoiceBox<String> templates;
    @FXML private Label wordsLabel;
    @FXML private Button generate;
    private AnalysisResult currentResult;

    public void initializeWithResult(AnalysisResult result) {
        // Salva il risultato come campo della classe
        this.currentResult = result;

        // Mostra il numero di variazioni
        number.setText("1");

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
            try {
                numSentences = Integer.parseInt(number.getText());
                if (numSentences <= 0) {
                    showAlert("Errore", "Il numero di frasi deve essere maggiore di 0");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Errore", "Inserisci un numero valido");
                return;
            }
            String template = templates.getValue().toString();
            if (template == null || template.isEmpty() || template.equals("-")) {
                showAlert("Errore", "Seleziona un template valido");
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

            // Mostra i risultati
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/results.fxml"));
            Parent root = loader.load();
            ResultsController controller = loader.getController();
            controller.setPreviousView("noTree"); // o "tree" per SyntaxTreeController
            controller.setAnalysisResult(currentResult);
            controller.showGeneratedSentences(sentences);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.setTitle("Results");
            stage.show();

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            showAlert("Errore", "Errore durante la generazione delle frasi: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            showAlert("Errore", e.getMessage());
        }
    }

    //gestione del menú per i template
    @FXML
    public void initialize() {
        try {
            // 1. Leggi tutte le righe dal file
            List<String> options = Files.readAllLines(Paths.get("src/resources/dictionaries/templates.txt"));

            // 2. Pulisci eventuali elementi esistenti e aggiungi i nuovi
            templates.getItems().clear();
            templates.getItems().addAll(options);

            // 3. Imposta un valore predefinito (opzionale)
            if (!options.isEmpty()) {
                templates.setValue("-");
            }

        } catch (IOException e) {
            System.err.println("Errore nel caricamento delle opzioni: " + e.getMessage());

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
}