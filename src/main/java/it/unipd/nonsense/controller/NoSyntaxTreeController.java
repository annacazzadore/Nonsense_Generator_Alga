package it.unipd.nonsense.controller;

import it.unipd.nonsense.utils.AnalysisResult;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;

public class NoSyntaxTreeController {
    @FXML private TextField number;
    @FXML private MenuButton template;

    public void initializeWithResult(AnalysisResult result) {
        // Mostra il numero di variazioni
        number.setText("1"); // Puoi modificare questo valore

        // Aggiungi i template al menu button
        template.getItems().clear();
        // Esempio: aggiungi i tipi di parole come template
        for (String type : result.getWordsByType().keySet()) {
            javafx.scene.control.MenuItem item = new javafx.scene.control.MenuItem(type);
            template.getItems().add(item);
        }
    }
    @FXML
    private void handleBack(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/main.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Random Sentence Generator ALGA");
            stage.show();

            // Chiudi la finestra attuale
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}