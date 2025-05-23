package it.unipd.nonsense.controller;

import it.unipd.nonsense.utils.AnalysisResult;
import it.unipd.nonsense.utils.DependencyTreeVisualizer;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.control.Button;

public class SyntaxTreeController extends SyntaxController {
    @FXML private Pane treePane;
    @FXML private Button generateT;
    private DependencyTreeVisualizer treeVisualizer;

    @Override
    public void initialize() {
        super.initialize(); // Chiama l'inizializzazione della classe base
        treeVisualizer = new DependencyTreeVisualizer(treePane);

        treePane.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (currentResult != null) {
                initializeWithResult(currentResult);
            }
        });

        treePane.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (currentResult != null) {
                initializeWithResult(currentResult);
            }
        });
    }

    @Override
    public void initializeWithResult(AnalysisResult result) {
        super.initializeWithResult(result);
        treeVisualizer.drawDependencyTree(result.getSyntaxResponse().getTokensList());
    }

    @Override
    protected String getPreviousViewType() {
        return "tree";
    }
}