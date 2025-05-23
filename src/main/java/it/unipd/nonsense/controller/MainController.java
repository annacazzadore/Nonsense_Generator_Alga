package it.unipd.nonsense.controller;

import it.unipd.nonsense.utils.AnalysisResult;
import it.unipd.nonsense.utils.SentenceAnalyzer;
import com.google.cloud.language.v1.AnalyzeSyntaxResponse;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class MainController
{

    @FXML private TextField sentence;
    @FXML private CheckBox tree;
    @FXML private Button analyze;

    @FXML
    private void analyzeSentence()
    {
        String inputText = sentence.getText().trim();

        if (!inputText.isEmpty())
        {
            try
            {
                SentenceAnalyzer analyzer = new SentenceAnalyzer();

                if (!analyzer.isEnglish(inputText))
                {
                    showError("Please avoid using non-English terms.");
                    analyzer.close();
                    return;
                }

                AnalyzeSyntaxResponse syntaxResponse = analyzer.analyzeSentence(inputText);

                float toxicityScore = analyzer.getToxicityScore(inputText);
                System.out.printf("Toxicity rate: %.2f%%%n", toxicityScore * 100);

                if (toxicityScore >= 0.5f)
                {
                    showErrorDialog(toxicityScore);
                    analyzer.close();
                    return;
                }

                Stage currentStage = (Stage) analyze.getScene().getWindow();

                FXMLLoader loader;
                if (tree.isSelected())
                {
                    loader = new FXMLLoader(getClass().getResource("/views/syntaxTree.fxml"));
                }
                else
                {
                    loader = new FXMLLoader(getClass().getResource("/views/noSyntaxTree.fxml"));
                }

                Parent root = loader.load();
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root));
                newStage.setTitle("Sentence Analyzer");
                newStage.setResizable(false);
                newStage.show();

                currentStage.close();

                AnalysisResult result = new AnalysisResult();
                result.setOriginalText(inputText);
                result.setSyntaxResponse(syntaxResponse);
                result.setWordsByType(analyzer.extractWordsByType(syntaxResponse));

                if (tree.isSelected())
                {
                    analyzer.printSyntaxTree(syntaxResponse);
                }

                analyzer.saveWordsToDictionaries(result.getWordsByType());

                if (tree.isSelected())
                {
                    SyntaxTreeController syntaxTreeController = loader.getController();
                    syntaxTreeController.initializeWithResult(result);
                }
                else
                {
                    NoSyntaxTreeController noSyntaxTreeController = loader.getController();
                    noSyntaxTreeController.initializeWithResult(result);
                }

                newStage.show();
                analyzer.close();

            }
            catch (Exception e)
            {
                e.printStackTrace();
                showError("An error occurred while analyzing the sentence : " + e.getMessage());
            }
        }
        else
        {
            showError("Please enter a sentence to analyze.");
        }
    }

    private void showErrorDialog(double toxicityScore)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/popUp.fxml"));
            Parent root = loader.load();

            PopUpController controller = loader.getController();
            controller.setToxicityRate(toxicityScore);

            Stage popUpStage = new Stage();
            popUpStage.setResizable(false);
            popUpStage.setScene(new Scene(root));
            popUpStage.setX(370);
            popUpStage.setY(265);
            popUpStage.setTitle("Error");
            popUpStage.initModality(Modality.APPLICATION_MODAL);
            popUpStage.initOwner(analyze.getScene().getWindow());
            popUpStage.showAndWait();

            sentence.clear();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void showError(String message)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setSentenceText(String text)
    {
        if (text != null && !text.trim().isEmpty())
        {
            sentence.setText(text.trim());
        }
    }

}
