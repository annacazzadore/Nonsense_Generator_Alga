package it.unipd.nonsense.controller;

import it.unipd.nonsense.utils.AnalysisResult;
import it.unipd.nonsense.utils.SentenceAnalyzer;
import com.google.cloud.language.v1.AnalyzeSyntaxResponse;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class MainController {

    @FXML
    private TextField sentence;
    @FXML
    private CheckBox tree;
    @FXML
    private Button analyze;

    private AnalysisResult lastAnalysisResult;

    @FXML
    private void analyzeSentence() {
        String inputText = sentence.getText().trim();
        if (!inputText.isEmpty()) {
            try {
                // Inizializza l'analizzatore di frasi
                SentenceAnalyzer analyzer = new SentenceAnalyzer();

                // Analizza la frase
                AnalyzeSyntaxResponse syntaxResponse = analyzer.analyzeSentence(inputText);

                // Calcola e stampa il punteggio di tossicità
                float toxicityScore = analyzer.getToxicityScore(inputText);
                System.out.printf("Livello di tossicità: %.2f%%%n", toxicityScore * 100);

// Soglia personalizzabile (es. 0.5 = 50%)
                if (toxicityScore >= 0.5f) {
                    showErrorDialog(
                            String.format("La frase contiene contenuto potenzialmente tossico (livello: %.2f%%)", toxicityScore * 100),
                            toxicityScore
                    );
                    analyzer.close();
                    return;
                }

                // Ottieni lo Stage corrente
                Stage currentStage = (Stage) analyze.getScene().getWindow();
                // Carica la finestra appropriata in base alla selezione del checkbox
                FXMLLoader loader;
                if (tree.isSelected()) {
                    loader = new FXMLLoader(getClass().getResource("/views/syntaxTree.fxml"));
                } else {
                    loader = new FXMLLoader(getClass().getResource("/views/noSyntaxTree.fxml"));
                }

                Parent root = loader.load();
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root));
                newStage.setTitle("Sentence Analyzer");
                newStage.setResizable(false);
                newStage.show();

                // Chiudi la finestra attuale
                currentStage.close();

                // Crea il risultato dell'analisi
                AnalysisResult result = new AnalysisResult();
                result.setOriginalText(inputText);
                result.setSyntaxResponse(syntaxResponse);
                result.setWordsByType(analyzer.extractWordsByType(syntaxResponse));

                // Stampa l'albero sintattico se richiesto
                if (tree.isSelected()) {
                    analyzer.printSyntaxTree(syntaxResponse);
                }

                // Salva le parole nei dizionari
                analyzer.saveWordsToDictionaries(result.getWordsByType());

                // Passa i risultati al controller appropriato
                if (tree.isSelected()) {
                    SyntaxTreeController syntaxTreeController = loader.getController();
                    syntaxTreeController.initializeWithResult(result);
                } else {
                    NoSyntaxTreeController noSyntaxTreeController = loader.getController();
                    noSyntaxTreeController.initializeWithResult(result);
                }
                this.lastAnalysisResult = result;
                newStage.show();
                analyzer.close();

            } catch (Exception e) {
                e.printStackTrace();
                showError("Errore durante l'analisi della frase: " + e.getMessage());
            }
        } else {
            showError("Inserisci una frase da analizzare");
        }
    }

    private void showErrorDialog(String message, double toxicityScore) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/popUp.fxml"));
            Parent root = loader.load();

            // Ottieni il controller del popUp e imposta il tasso di tossicità
            PopUpController controller = loader.getController();
            controller.setToxicityRate(toxicityScore);

            Stage popUpStage = new Stage();
            popUpStage.setResizable(false);
            popUpStage.setScene(new Scene(root));
            popUpStage.setTitle("Error");
            popUpStage.initModality(Modality.APPLICATION_MODAL);
            popUpStage.initOwner((Stage) analyze.getScene().getWindow());
            popUpStage.showAndWait();

            sentence.clear();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        // Implementa una finestra di dialogo per mostrare gli errori
        // Puoi usare Alert di JavaFX o un altro metodo a tua scelta
        System.err.println(message);
    }

    public void setSentenceText(String text) {
        if (text != null && !text.trim().isEmpty()) {
            sentence.setText(text.trim());
        }
    }

    public void setAnalysisResult(AnalysisResult result) {
        this.lastAnalysisResult = result;
    }
}