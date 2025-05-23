package it.unipd.nonsense.controller;

import it.unipd.nonsense.utils.AnalysisResult;
import it.unipd.nonsense.utils.DependencyTreeVisualizer;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class SyntaxTreeController extends SyntaxController {
    private static final double SIZE_CHANGE_THRESHOLD = 5.0;
    public ImageView back;
    public ScrollPane treeScrollPane;
    public Button generateT;

    @FXML
    private Pane treePane;

    private DependencyTreeVisualizer treeVisualizer;
    private final ChangeListener<Number> sizeListener = this::handlePaneSizeChange;

    @Override
    public void initialize() {
        super.initialize();
        initializeTreeVisualizer();
        setupSizeListeners();
    }

    @Override
    public void initializeWithResult(AnalysisResult result) {
        super.initializeWithResult(result);
        redrawDependencyTree(result);
    }

    @Override
    protected String getPreviousViewType() {
        return "tree";
    }

    private void initializeTreeVisualizer() {
        if (treeVisualizer == null) {
            treeVisualizer = new DependencyTreeVisualizer(treePane);
        }
    }

    private void setupSizeListeners() {
        treePane.widthProperty().addListener(sizeListener);
        treePane.heightProperty().addListener(sizeListener);
    }

    private void handlePaneSizeChange(ObservableValue<? extends Number> obs,
                                      Number oldVal, Number newVal) {
        if (currentResult != null &&
                Math.abs(oldVal.doubleValue() - newVal.doubleValue()) > SIZE_CHANGE_THRESHOLD) {
            Platform.runLater(() -> redrawDependencyTree(currentResult));
        }
    }

    private void redrawDependencyTree(AnalysisResult result) {
        if (result != null && result.getSyntaxResponse() != null) {
            treeVisualizer.drawDependencyTree(result.getSyntaxResponse().getTokensList());
        }
    }
}