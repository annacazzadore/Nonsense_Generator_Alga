package it.unipd.nonsense.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class PopUpController {
    @FXML
    private TextField toxicity; // Aggiungi un fx:id corrispondente in popUp.fxml

    public void setToxicityRate(double rate) {
        toxicity.setText(String.format("%.2f%%", rate * 100));
    }
}