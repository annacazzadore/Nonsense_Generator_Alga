package it.unipd.nonsense.controller;

import it.unipd.nonsense.utils.AnalysisResult;
import it.unipd.nonsense.utils.SentenceGenerator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Abstract base controller for syntax-related views in the application.
 * Handles common functionality for sentence generation and navigation between views.
 */
public abstract class SyntaxController {
    // File paths and constants
    private static final String TEMPLATES_FILE = "src/main/resources/it/unipd/nonsense/dictionaries/templates.txt";
    private static final String DEFAULT_TEMPLATE = "-";
    private static final String MAIN_VIEW = "/it/unipd/nonsense/views/Main.fxml";
    private static final String RESULTS_VIEW = "/it/unipd/nonsense/views/results.fxml";
    private static final String APP_TITLE = "Random Sentence Generator ALGA";

    // Constants for word types
    private static final String NOUN = "NOUN";
    private static final String VERB = "VERB";
    private static final String ADJ = "ADJ";

    // FXML injected UI components
    @FXML protected Spinner<Integer> number;      // Spinner for number of sentences to generate
    @FXML protected ChoiceBox<String> templates;  // Dropdown for template selection
    @FXML protected Label wordsLabel;             // Label to display extracted words

    // Controller state
    protected AnalysisResult currentResult;        // Current analysis results
    protected int numberValue = 1;                // Default number of sentences
    protected String selectedTemplate = DEFAULT_TEMPLATE; // Selected template

    /**
     * Initializes the controller when FXML is loaded.
     * Sets up UI components with default values.
     */
    @FXML
    public void initialize() {
        initializeTemplate();
    }

    /**
     * Abstract method to be implemented by subclasses.
     * @return The type of previous view (used for navigation)
     */
    protected abstract String getPreviousViewType();

    /**
     * Initializes the controller with analysis results.
     * @param result The analysis results to display
     */
    public void initializeWithResult(AnalysisResult result) {
        this.currentResult = result;
        updateWordsLabel();
    }

    /**
     * Initializes template-related UI components.
     */
    @FXML
    public void initializeTemplate() {
        initializeNumberSpinner();
        initializeTemplateChoiceBox();
    }

    /**
     * Configures the number spinner with valid range and default value.
     */
    private void initializeNumberSpinner() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, numberValue);
        number.setValueFactory(valueFactory);
    }

    /**
     * Loads templates from file and fills the choice box.
     */
    private void initializeTemplateChoiceBox() {
        try {
            List<String> options = Files.readAllLines(Paths.get(TEMPLATES_FILE));
            templates.getItems().setAll(options);
            templates.setValue(selectedTemplate != null ? selectedTemplate : DEFAULT_TEMPLATE);
        } catch (IOException e) {
            System.err.println("Failed to load templates: " + e.getMessage());
            templates.setValue(DEFAULT_TEMPLATE);
        }
    }

    /**
     * Displays an error alert dialog.
     * @param message The error message to display
     */
    protected void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Handles navigation back to the main view.
     * @param event The mouse event that triggered this action
     */
    @FXML
    protected void handleBack(MouseEvent event) {
        navigateToView(event, controller -> ((MainController) controller).setSentenceText(currentResult.getOriginalText()));
    }

    /**
     * Generic method for navigating to a view.
     * @param event The triggering mouse event
     * @param controllerConfigurator Consumer to configure the target controller
     */
    private void navigateToView(MouseEvent event, Consumer<Object> controllerConfigurator) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(MAIN_VIEW));
            Parent root = loader.load();

            controllerConfigurator.accept(loader.getController());

            Stage stage = createStage(root, APP_TITLE);
            stage.show();

            closeCurrentStage(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new stage with common configuration.
     * @param root The root node of the scene
     * @param title The window title
     * @return The configured Stage
     */
    private Stage createStage(Parent root, String title) {
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.setTitle(title);
        return stage;
    }

    /**
     * Closes the current stage.
     * @param event The mouse event that contains stage information
     */
    private void closeCurrentStage(MouseEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }

    /**
     * Updates the words label with extracted words from the analysis.
     */
    private void updateWordsLabel() {
        Map<String, Set<String>> wordsByType = currentResult.getWordsByType();
        String joinedWords = Stream.of(NOUN, VERB, ADJ).filter(wordsByType::containsKey).flatMap(type -> wordsByType.get(type).stream()).collect(Collectors.joining(", "));
        wordsLabel.setText(joinedWords);
    }

    /**
     * Handles sentence generation when the generate button is clicked.
     * @param event The mouse event that triggered this action
     */
    @FXML
    protected void handleGenerate(MouseEvent event) {
        try {
            validateInputs();

            this.selectedTemplate = templates.getValue();
            this.numberValue = number.getValue();

            List<String> sentences = generateSentences();
            navigateToResultsView(event, sentences);

        } catch (IllegalArgumentException e) {
            showAlert(e.getMessage());
        } catch (IOException e) {
            showAlert("An error occurred while generating the sentences: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Validates user inputs before generation.
     * @throws IllegalArgumentException if inputs are invalid
     */
    private void validateInputs() {
        if (number.getValue() <= 0) {
            throw new IllegalArgumentException("The number of sentences must be greater than 0.");
        }

        String template = templates.getValue();
        if (template == null || template.isEmpty() || template.equals(DEFAULT_TEMPLATE)) {
            throw new IllegalArgumentException("Select a valid template.");
        }
    }

    /**
     * Generates sentences based on current settings.
     * @return List of generated sentences
     */
    private List<String> generateSentences() {
        Map<String, Set<String>> wordsByType = currentResult.getWordsByType();

        List<String> nouns = new ArrayList<>(wordsByType.getOrDefault(NOUN, Collections.emptySet()));
        List<String> verbs = new ArrayList<>(wordsByType.getOrDefault(VERB, Collections.emptySet()));
        List<String> adjectives = new ArrayList<>(wordsByType.getOrDefault(ADJ, Collections.emptySet()));

        try {
            return new SentenceGenerator().generateSentences(numberValue, selectedTemplate, nouns, verbs, adjectives);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Navigates to the results view with generated sentences.
     * @param event The triggering mouse event
     * @param sentences The sentences to display in results view
     * @throws IOException if view loading fails
     */
    private void navigateToResultsView(MouseEvent event, List<String> sentences) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(RESULTS_VIEW));
        Parent root = loader.load();

        ResultsController controller = loader.getController();
        configureResultsController(controller, sentences);

        Stage stage = createStage(root, "Results");
        stage.show();

        closeCurrentStage(event);
    }

    /**
     * Configures the results controller before showing the view.
     * @param controller The results controller to configure
     * @param sentences The sentences to display
     */
    private void configureResultsController(ResultsController controller, List<String> sentences) {
        controller.setPreviousView(getPreviousViewType());
        controller.setAnalysisResult(currentResult);
        controller.showGeneratedSentences(sentences);
        controller.setNumberValue(numberValue);
        controller.setSelectedTemplate(selectedTemplate);
    }

    /**
     * Sets the number of sentences to generate.
     * @param value The number value
     */
    public void setNumberValue(int value) {
        this.numberValue = value;
        if (number != null && number.getValueFactory() != null) {
            number.getValueFactory().setValue(value);
        }
    }

    /**
     * Sets the selected template.
     * @param template The template to select
     */
    public void setSelectedTemplate(String template) {
        this.selectedTemplate = template;
        if (templates != null) {
            templates.setValue(template);
        }
    }
}