package it.unipd.nonsense.controller;

import it.unipd.nonsense.utils.AnalysisResult;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;

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
}