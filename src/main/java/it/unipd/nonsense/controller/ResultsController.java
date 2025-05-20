package it.unipd.nonsense.controller;

import it.unipd.nonsense.utils.AnalysisResult;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.util.List;

public class ResultsController {
    @FXML
    private Button end;
    @FXML
    private TextArea result;
    @FXML
    private ImageView back;
    @FXML
    private TextArea resultArea;

    private String previousView; // "tree" o "noTree
    private AnalysisResult analysisResult;
    private String originalText; // testo originale della frase analizzata

    public void setPreviousView(String previousView) {
        this.previousView = previousView;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public void setResultText(String text) {
        result.setText(text);
    }

    public void setAnalysisResult(AnalysisResult analysisResult) {
        this.analysisResult = analysisResult;
    }

    @FXML
    private void handleEnd(ActionEvent event) {
        Stage stage = (Stage) end.getScene().getWindow();
        stage.close();

        // Termina completamente l'applicazione
        Platform.exit();
    }

    @FXML
    private void handleBack(MouseEvent event) {
        try {
            FXMLLoader loader;
            if ("tree".equals(previousView)) {
                loader = new FXMLLoader(getClass().getResource("/views/syntaxTree.fxml"));
            } else {
                loader = new FXMLLoader(getClass().getResource("/views/noSyntaxTree.fxml"));
            }

            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.setTitle("Sentence Analyzer");
            stage.show();

            // Passa il testo originale al controller
            if ("tree".equals(previousView)) {
                SyntaxTreeController controller = loader.getController();
                controller.initializeWithResult(analysisResult);
            } else {
                NoSyntaxTreeController controller = loader.getController();
                controller.initializeWithResult(analysisResult);
            }

            // Chiudi la finestra corrente
            Stage currentStage = (Stage) back.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showGeneratedSentences(List<String> sentences) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sentences.size(); i++) {
            sb.append((i + 1)).append(". ").append(sentences.get(i)).append("\n\n");
        }
        resultArea.setText(sb.toString());
    }

}