package it.unipd.nonsense.controller;

import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

/**
 * Controller for the view without syntax tree visualization.
 * Extends the base {@link SyntaxController} to provide specific behavior
 * for the simplified sentence analysis view.
 */
public class NoSyntaxTreeController extends SyntaxController {
    /**
     * Constant identifying this view type for navigation purposes
     */
    private static final String VIEW_TYPE = "noTree";

    /**
     * Back button with custom image/icon
     */
    public ImageView back;

    /**
     * Button to trigger sentence generation
     */
    public Button generate;

    /**
     * Initializes the controller after FXML loading.
     * Calls the parent class initialization to set up common components.
     */
    @Override
    public void initialize() {
        super.initialize();  // Initialize base class components
    }

    /**
     * Gets the view type identifier for navigation.
     *
     * @return The constant string "noTree" identifying this view type
     */
    @Override
    protected String getPreviousViewType() {
        return VIEW_TYPE;
    }
}