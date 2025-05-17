package it.unipd.nonsense.controller;

import it.unipd.nonsense.utils.AnalysisResult;
import it.unipd.nonsense.utils.SentenceAnalyzer;
import com.google.cloud.language.v1.AnalyzeSyntaxResponse;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class MainController {

    @FXML
    private TextField sentence;

    @FXML
    private CheckBox tree;

    @FXML
    private void analyzeSentence() {
        String inputText = sentence.getText().trim();
        if (!inputText.isEmpty()) {
            try {
                // Inizializza l'analizzatore di frasi
                SentenceAnalyzer analyzer = new SentenceAnalyzer();

                // Analizza la frase
                AnalyzeSyntaxResponse syntaxResponse = analyzer.analyzeSentence(inputText);

                // Verifica la tossicità della frase
                if (!analyzer.validateToxicity(inputText)) {
                    showErrorDialog("La frase contiene contenuto potenzialmente tossico");
                    analyzer.close();
                    return;
                }

                // Carica la finestra appropriata in base alla selezione del checkbox
                FXMLLoader loader;
                if (tree.isSelected()) {
                    loader = new FXMLLoader(getClass().getResource("/views/syntaxTree.fxml"));
                } else {
                    loader = new FXMLLoader(getClass().getResource("/views/noSyntaxTree.fxml"));
                }

                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Sentence Analyzer");

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

                stage.show();
                analyzer.close();

            } catch (Exception e) {
                e.printStackTrace();
                showErrorDialog("Errore durante l'analisi della frase: " + e.getMessage());
            }
        } else {
            showErrorDialog("Inserisci una frase da analizzare");
        }
    }

    private void showErrorDialog(String message) {
        // Implementa una finestra di dialogo per mostrare gli errori
        // Puoi usare Alert di JavaFX o un altro metodo a tua scelta
        System.err.println(message);
    }
}