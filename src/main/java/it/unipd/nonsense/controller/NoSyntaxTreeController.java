package it.unipd.nonsense.controller;

import it.unipd.nonsense.utils.AnalysisResult;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class NoSyntaxTreeController extends SyntaxController {
    @FXML private Button generate;

    @Override
    public void initialize() {
        super.initialize(); // Chiama l'inizializzazione della classe base
        // Eventuale logica specifica qui (se necessaria)
    }

    @Override
    protected String getPreviousViewType() {
        return "noTree";
    }
}