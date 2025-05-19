package it.unipd.nonsense.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.application.Platform;

public class ResultsController {
    @FXML
    private Button end; // Riferimento al pulsante end

    @FXML
    private void handleEnd(ActionEvent event) {
        // Chiude la finestra corrente
        Stage stage = (Stage) end.getScene().getWindow();
        stage.close();

        // Termina completamente l'applicazione
        Platform.exit();
    }
}