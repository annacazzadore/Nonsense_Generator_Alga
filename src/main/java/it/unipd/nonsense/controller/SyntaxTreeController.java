package it.unipd.nonsense.controller;

import com.google.cloud.language.v1.Token;
import it.unipd.nonsense.utils.AnalysisResult;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.scene.Node;


import java.io.IOException;

public class SyntaxTreeController {
    @FXML private TextField numberT;
    @FXML private MenuButton templateT;
    @FXML private TextFlow syntaxTreeDisplay;

    public void initializeWithResult(AnalysisResult result) {
        // Mostra il numero di variazioni
        numberT.setText("1"); // Puoi modificare questo valore

        // Popola l'albero sintattico
        StringBuilder treeBuilder = new StringBuilder();
        for (Token token : result.getSyntaxResponse().getTokensList()) {
            treeBuilder.append(String.format("Parola: %-10s Tipo: %s%n",
                    token.getText().getContent(),
                    token.getPartOfSpeech().getTag()));
        }

    }
    @FXML
    private void handleBack(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Main Window");
            stage.show();

            // Chiudi la finestra attuale
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}