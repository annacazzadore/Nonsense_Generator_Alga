package it.unipd.nonsense.controller;

import it.unipd.nonsense.utils.AnalysisResult;
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
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

public class NoSyntaxTreeController {
    @FXML private TextField number;
    @FXML private MenuButton template;
    @FXML private TextField words;

    public void initializeWithResult(AnalysisResult result) {
        // Mostra il numero di variazioni
        number.setText("1");

        // Aggiungi i template al menu button
        template.getItems().clear();
        for (String type : result.getWordsByType().keySet()) {
            javafx.scene.control.MenuItem item = new javafx.scene.control.MenuItem(type);
            template.getItems().add(item);
        }

        // Prepara il testo da mostrare nel TextField
        StringJoiner wordsJoiner = new StringJoiner(", ");
        Map<String, Set<String>> wordsByType = result.getWordsByType(); // Usa Set<String> invece di List<String>

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
        words.setText(wordsJoiner.toString());
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