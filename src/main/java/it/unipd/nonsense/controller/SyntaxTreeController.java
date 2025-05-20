package it.unipd.nonsense.controller;

import com.google.cloud.language.v1.Token;
import it.unipd.nonsense.utils.AnalysisResult;
import it.unipd.nonsense.utils.DependencyTreeVisualizer;
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
import java.util.List;

public class SyntaxTreeController {
    @FXML private TextField numberT;
    @FXML private ChoiceBox templates;
    @FXML private Pane treePane; // Cambiato da TextFlow a Pane per disegnare
    @FXML private Button generateT;
    private AnalysisResult currentResult;

    private DependencyTreeVisualizer treeVisualizer;

    @FXML
    public void initialize() {

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

        this.currentResult = result;

        numberT.setText("1");

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
            // Carica la finestra results.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/results.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Results");
            stage.show();

            // Chiudi la finestra attuale
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //gestione del menú per i template
    @FXML
    public void initializeTemplate() {
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
}