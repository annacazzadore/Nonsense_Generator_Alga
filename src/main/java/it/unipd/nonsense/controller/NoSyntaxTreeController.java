package it.unipd.nonsense.controller;

import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

public class NoSyntaxTreeController extends SyntaxController
{
    private static final String VIEW_TYPE = "noTree";
    public ImageView back;
    public Button generate;

    @Override
    public void initialize() {
        super.initialize();
    }

    @Override
    protected String getPreviousViewType() {
        return VIEW_TYPE;
    }
}