package it.unipd.nonsense.controller;

import com.google.cloud.language.v1.Token;
import it.unipd.nonsense.utils.AnalysisResult;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.text.TextFlow;

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

        // Qui dovresti implementare la visualizzazione grafica dell'albero sintattico
        // invece di usare semplicemente un TextFlow
    }
}