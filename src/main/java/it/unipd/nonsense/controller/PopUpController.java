package it.unipd.nonsense.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class PopUpController
{
    private static final String TOXICITY_RATE_FORMAT = "%.2f%%";
    private static final double PERCENTAGE_MULTIPLIER = 100.0;

    @FXML private TextField toxicity;

    /**
     * Sets the toxicity rate in the text field with percentage formatting
     * @param rate Toxicity rate between 0.0 and 1.0
     * @throws IllegalArgumentException if rate is not between 0.0 and 1.0
     */
    public void setToxicityRate(final double rate)
    {
        validateToxicityRate(rate);
        final String formattedRate = formatToxicityRate(rate);
        toxicity.setText(formattedRate);
    }

    private void validateToxicityRate(final double rate)
    {
        if (rate < 0.0 || rate > 1.0)
        {
            throw new IllegalArgumentException(String.format("Toxicity rate must be between 0.0 and 1.0. Received: %f", rate));
        }
    }

    private String formatToxicityRate(final double rate)
    {
        return String.format(TOXICITY_RATE_FORMAT, rate * PERCENTAGE_MULTIPLIER);
    }
}