package it.unipd.nonsense.controller;

import it.unipd.nonsense.main;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

public class MainController {
    @FXML
    private Button generator;  // Deve corrispondere all'fx:id del pulsante in FXML

    @FXML
    private void initialize() {
        // Codice eseguito all'avvio (opzionale)
    }

    @FXML
    private void handleButtonClick(ActionEvent event) throws Exception{
        main.cambiaScene("Seconda.fxml");
    }
}