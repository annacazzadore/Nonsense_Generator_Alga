package it.unipd.nonsense.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * Controller for the toxicity warning popup dialog.
 * Handles the display of toxicity percentage when potentially offensive content is detected.
 */
public class PopUpController {
    /**
     * Format string for displaying toxicity percentage (2 decimal places with % sign)
     */
    private static final String TOXICITY_RATE_FORMAT = "%.2f%%";

    /**
     * Multiplier to convert decimal rate to percentage (0.5 → 50%)
     */
    private static final double PERCENTAGE_MULTIPLIER = 100.0;

    /**
     * Text field that displays the calculated toxicity percentage
     */
    @FXML private TextField toxicity;

    /**
     * Sets and displays the toxicity rate in percentage format.
     * Validates the input rate and formats it for display.
     *
     * @param rate Toxicity score between 0.0 (non-toxic) and 1.0 (highly toxic)
     * @throws IllegalArgumentException if rate is outside valid range [0.0, 1.0]
     */
    public void setToxicityRate(final double rate) {
        validateToxicityRate(rate);
        final String formattedRate = formatToxicityRate(rate);
        toxicity.setText(formattedRate);
    }

    /**
     * Validates that the toxicity rate is within acceptable bounds.
     *
     * @param rate Toxicity score to validate
     * @throws IllegalArgumentException if rate is outside [0.0, 1.0] range
     */
    private void validateToxicityRate(final double rate) {
        if (rate < 0.0 || rate > 1.0) {
            throw new IllegalArgumentException(String.format("Toxicity rate must be between 0.0 and 1.0. Received: %f", rate));
        }
    }

    /**
     * Formats the toxicity rate as a percentage string.
     *
     * @param rate Toxicity score to format
     * @return Formatted percentage string (e.g., "75.00%")
     */
    private String formatToxicityRate(final double rate) {
        return String.format(TOXICITY_RATE_FORMAT, rate * PERCENTAGE_MULTIPLIER);
    }
}