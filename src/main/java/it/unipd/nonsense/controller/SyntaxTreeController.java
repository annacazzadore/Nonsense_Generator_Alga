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

/**
 * Controller for the syntax tree visualization view.
 * Extends the base {@link SyntaxController} to provide specialized functionality for displaying
 * and managing dependency parse tree visualizations.
 */
public class SyntaxTreeController extends SyntaxController {
    /**
     * Minimum size change (in pixels) required to trigger tree redraw
     */
    private static final double SIZE_CHANGE_THRESHOLD = 5.0;

    /**
     * Visualizer component that handles the actual tree drawing
     */
    private DependencyTreeVisualizer treeVisualizer;

    /**
     * Listener for pane size changes to enable responsive tree redrawing
     */
    private final ChangeListener<Number> sizeListener = this::handlePaneSizeChange;

    // FXML-injected UI components
    public ImageView back;               // Back navigation button
    public ScrollPane treeScrollPane;    // Scroll container for the tree
    public Button generateT;             // Generate sentences button
    @FXML private Pane treePane;         // Drawing surface for the dependency tree

    /**
     * Initializes the controller after FXML loading.
     * Sets up the tree visualizer and size change listeners.
     */
    @Override
    public void initialize() {
        super.initialize();  // Initialize base class components
        initializeTreeVisualizer();
        setupSizeListeners();
    }

    /**
     * Initializes the controller with analysis results.
     * Draws the dependency tree visualization.
     *
     * @param result The analysis results containing syntax information
     */
    @Override
    public void initializeWithResult(AnalysisResult result) {
        super.initializeWithResult(result);
        redrawDependencyTree(result);
    }

    /**
     * Gets the view type identifier for navigation purposes.
     *
     * @return The string "tree" identifying this view type
     */
    @Override
    protected String getPreviousViewType() {
        return "tree";
    }

    /**
     * Initializes the tree visualizer component if not already created.
     */
    private void initializeTreeVisualizer() {
        if (treeVisualizer == null) {
            treeVisualizer = new DependencyTreeVisualizer(treePane);
        }
    }

    /**
     * Sets up listeners for pane size changes to enable responsive redrawing.
     */
    private void setupSizeListeners() {
        treePane.widthProperty().addListener(sizeListener);
        treePane.heightProperty().addListener(sizeListener);
    }

    /**
     * Handles pane size changes and triggers tree redraw when significant.
     *
     * @param obs The observable property that changed
     * @param oldVal The previous size value
     * @param newVal The new size value
     */
    private void handlePaneSizeChange(ObservableValue<? extends Number> obs,
                                      Number oldVal, Number newVal) {
        // Only redraw if change exceeds threshold to avoid excessive redraws
        if (currentResult != null && Math.abs(oldVal.doubleValue() - newVal.doubleValue()) > SIZE_CHANGE_THRESHOLD) {
            Platform.runLater(() -> redrawDependencyTree(currentResult));
        }
    }

    /**
     * Redraws the dependency tree visualization with current analysis results.
     *
     * @param result The analysis results containing syntax information
     */
    private void redrawDependencyTree(AnalysisResult result) {
        if (result != null && result.getSyntaxResponse() != null) {
            treeVisualizer.drawDependencyTree(result.getSyntaxResponse().getTokensList());
        }
    }
}