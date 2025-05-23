package it.unipd.nonsense.controller;

import com.google.cloud.language.v1.Token;
import it.unipd.nonsense.utils.AnalysisResult;
import it.unipd.nonsense.utils.DependencyTreeVisualizer;
import it.unipd.nonsense.utils.SentenceGenerator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class SyntaxTreeController {

    @FXML private Spinner<Integer> number;
    @FXML private ChoiceBox templates;
    @FXML private Label wordsLabel;
    @FXML private Pane treePane; // Cambiato da TextFlow a Pane per disegnare
    @FXML private Button generateT;
    private AnalysisResult currentResult;

    private DependencyTreeVisualizer treeVisualizer;
    private int numberValue = 1; // valore di default
    private String selectedTemplate = "-";



    @FXML
    public void initialize() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, numberValue); // min, max, initial
        number.setValueFactory(valueFactory);
        initializeTemplate();

        treeVisualizer = new DependencyTreeVisualizer(treePane);

        // Aggiungi listener per il ridimensionamento
        treePane.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (currentResult != null) {
                initializeWithResult(currentResult);
            }
        });

        treePane.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (currentResult != null) {
                initializeWithResult(currentResult);
            }
        });
    }

    public void initializeWithResult(AnalysisResult result) {
        // Salva il risultato come campo della classe
        this.currentResult = result;

        // Mostra il numero di variazioni
        //numberT.setText("1");

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

        DependencyTreeVisualizer visualizer = new DependencyTreeVisualizer(treePane);
        visualizer.drawDependencyTree(result.getSyntaxResponse().getTokensList());
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
            stage.setTitle("Random Sentence Generator ALGA");
            stage.show();

            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleGenerate(MouseEvent event) {
        try {
            // Ottieni e valida i parametri dall'interfaccia
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
            resultsController.setPreviousView("tree");
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    //gestione del menú per i template
    @FXML
    public void initializeTemplate() {
        try {
            // Carica i template
            List<String> options = Files.readAllLines(Paths.get("src/resources/dictionaries/templates.txt"));
            templates.getItems().clear();
            templates.getItems().addAll(options);

            // Imposta il valore salvato o il default
            if (!options.isEmpty()) {
                templates.setValue(selectedTemplate != null ? selectedTemplate : "-");
            }
        } catch (IOException e) {
            System.err.println("Failed to load options." + e.getMessage());
            templates.setValue("-");
        }
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