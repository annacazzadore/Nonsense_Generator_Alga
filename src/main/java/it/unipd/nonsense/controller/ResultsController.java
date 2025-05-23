package it.unipd.nonsense.controller;

import it.unipd.nonsense.utils.AnalysisResult;
import it.unipd.nonsense.utils.SentenceAnalyzer;
import it.unipd.nonsense.utils.SentenceGenerator;
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

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
    private int numberValue;
    private String selectedTemplate;


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

    public void setSelectedTemplate(String template) {
        this.selectedTemplate = template;
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
                controller.setNumberValue(numberValue);
                controller.setSelectedTemplate(selectedTemplate); // Passa il template
            } else {
                NoSyntaxTreeController controller = loader.getController();
                controller.initializeWithResult(analysisResult);
                controller.setNumberValue(numberValue);
                controller.setSelectedTemplate(selectedTemplate); // Passa il template
            }

            // Chiudi la finestra corrente
            Stage currentStage = (Stage) back.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveSentencesToFile(List<String> sentences, String filePath) {
        Path path = Paths.get(filePath);
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (int i = 0; i < sentences.size(); i++) {
                writer.write((i + 1) + ". " + sentences.get(i));
                writer.newLine();
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void showGeneratedSentences(List<String> inputSentences) {
        try {
            // Numero di frasi che vuoi mostrare
            int desiredCount = inputSentences.size();
            List<String> nonToxicSentences = new ArrayList<>();
            int attempts = 0;
            int maxAttempts = desiredCount * 10;

            SentenceAnalyzer analyzer = new SentenceAnalyzer();

            while (nonToxicSentences.size() < desiredCount && attempts < maxAttempts) {
                String sentence = inputSentences.get(attempts % inputSentences.size());
                float toxicity = analyzer.getToxicityScore(sentence);
                if (toxicity < 0.5f) {
                    nonToxicSentences.add(sentence);
                }
                attempts++;
            }

            analyzer.close();

            // Mostra solo le non tossiche
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < nonToxicSentences.size(); i++) {
                sb.append((i + 1)).append(". ").append(nonToxicSentences.get(i)).append("\n\n");
            }
            resultArea.setText(sb.toString());

            // Salva nel file
            saveSentencesToFile(nonToxicSentences, "generated_sentences.txt");

        } catch (Exception e) {
            e.printStackTrace();
            resultArea.setText("Error during sentence generation and filtering.");
        }
    }

    public void setNumberValue(int value) {
        this.numberValue = value;
    }

}