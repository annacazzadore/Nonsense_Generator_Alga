package it.unipd.nonsense.controller;

import it.unipd.nonsense.utils.AnalysisResult;
import it.unipd.nonsense.utils.SentenceAnalyzer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResultsController
{
    private static final Logger LOGGER = Logger.getLogger(ResultsController.class.getName());
    private static final String GENERATED_SENTENCES_FILE = "generated_sentences.txt";
    private static final float TOXICITY_THRESHOLD = 0.5f;
    private static final int MAX_ATTEMPTS_MULTIPLIER = 10;

    @FXML private Button end;
    @FXML private ImageView back;
    @FXML private TextArea resultArea;

    private String previousView;
    private AnalysisResult analysisResult;
    private int numberValue;
    private String selectedTemplate;
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public void setPreviousView(String previousView)
    {
        this.previousView = Objects.requireNonNull(previousView, "Previous view cannot be null");
    }

    public void setAnalysisResult(AnalysisResult analysisResult)
    {
        this.analysisResult = Objects.requireNonNull(analysisResult, "Analysis result cannot be null");
    }

    public void setSelectedTemplate(String template)
    {
        this.selectedTemplate = template;
    }

    public void setNumberValue(int value)
    {
        this.numberValue = value;
    }

    @FXML
    private void handleEnd()
    {
        Stage stage = (Stage) end.getScene().getWindow();
        stage.close();
        Platform.exit();
    }

    @FXML
    private void handleBack()
    {
        try
        {
            String fxmlFile = "tree".equals(previousView) ? "/views/syntaxTree.fxml" : "/views/noSyntaxTree.fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.setTitle("Sentence Analyzer");
            stage.show();

            initializePreviousController(loader);

            Stage currentStage = (Stage) back.getScene().getWindow();
            currentStage.close();

        }
        catch (IOException e)
        {
            LOGGER.log(Level.SEVERE, "Error while navigating back", e);
            showErrorAlert("Navigation Error", "Unable to load the previous view");
        }
    }

    private void initializePreviousController(FXMLLoader loader)
    {
        if ("tree".equals(previousView))
        {
            SyntaxTreeController controller = loader.getController();
            controller.initializeWithResult(analysisResult);
            controller.setNumberValue(numberValue);
            controller.setSelectedTemplate(selectedTemplate);
        }
        else
        {
            NoSyntaxTreeController controller = loader.getController();
            controller.initializeWithResult(analysisResult);
            controller.setNumberValue(numberValue);
            controller.setSelectedTemplate(selectedTemplate);
        }
    }

    public void saveSentencesToFile(List<String> sentences, String filePath)
    {
        Path path = Paths.get(filePath);

        try (BufferedWriter writer = Files.newBufferedWriter(path))
        {
            for (int i = 0; i < sentences.size(); i++)
            {
                writer.write(String.format("%d. %s%n%n", i + 1, sentences.get(i)));
            }
        }
        catch (IOException e)
        {
            LOGGER.log(Level.SEVERE, "Error while saving sentences to file", e);
            showErrorAlert("Save Error", "Unable to save sentences to file");
        }
    }

    public void showGeneratedSentences(List<String> inputSentences)
    {
        Objects.requireNonNull(inputSentences, "Input sentences cannot be null");
        resultArea.clear();
        executorService.shutdownNow();
        executorService = Executors.newSingleThreadScheduledExecutor();

        executorService.execute(() -> {
            List<String> allSentences = new ArrayList<>();
            List<String> nonToxicSentences = new ArrayList<>();

            try (SentenceAnalyzer analyzer = new SentenceAnalyzer())
            {
                int sentenceIndex = 1;

                for (String sentence : inputSentences)
                {
                    float toxicityScore = analyzer.getToxicityScore(sentence);

                    String resultText = String.format("%d. %s\nToxicity: %.2f%%\n\n", sentenceIndex, sentence, toxicityScore * 100);

                    Platform.runLater(() -> resultArea.appendText(resultText));

                    allSentences.add(sentence);
                    sentenceIndex++;

                    if (allSentences.size() >= numberValue)
                    {
                        break;
                    }

                    if (toxicityScore < 0.5)
                    {
                        nonToxicSentences.add(sentence);
                    }
                }
            }
            catch (Exception e)
            {
                LOGGER.log(Level.SEVERE, "Error analyzing sentence toxicity", e);
            }

            saveSentencesToFile(nonToxicSentences, GENERATED_SENTENCES_FILE);
            executorService.shutdown();
        });
    }

    private void showErrorAlert(String title, String message)
    {
        LOGGER.warning(title + ": " + message);
    }
}