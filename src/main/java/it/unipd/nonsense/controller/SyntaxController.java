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

public abstract class SyntaxController {
    private static final String TEMPLATES_FILE = "src/resources/dictionaries/templates.txt";
    private static final String DEFAULT_TEMPLATE = "-";
    private static final String MAIN_VIEW = "/views/main.fxml";
    private static final String RESULTS_VIEW = "/views/results.fxml";
    private static final String APP_TITLE = "Random Sentence Generator ALGA";

    private static final String NOUN = "NOUN";
    private static final String VERB = "VERB";
    private static final String ADJ = "ADJ";

    @FXML protected Spinner<Integer> number;
    @FXML protected ChoiceBox<String> templates;
    @FXML protected Label wordsLabel;

    protected AnalysisResult currentResult;
    protected int numberValue = 1;
    protected String selectedTemplate = DEFAULT_TEMPLATE;

    @FXML
    public void initialize() {
        initializeTemplate();
    }

    @FXML
    public void initializeTemplate() {
        initializeNumberSpinner();
        initializeTemplateChoiceBox();
    }

    private void initializeNumberSpinner() {
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, numberValue);
        number.setValueFactory(valueFactory);
    }

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

    protected void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    protected void handleBack(MouseEvent event) {
        navigateToView(event, controller -> ((MainController) controller).setSentenceText(currentResult.getOriginalText()));
    }

    private void navigateToView(MouseEvent event, Consumer<Object> controllerConfigurator) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(SyntaxController.MAIN_VIEW));
            Parent root = loader.load();

            controllerConfigurator.accept(loader.getController());

            Stage stage = createStage(root, APP_TITLE);
            stage.show();

            closeCurrentStage(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Stage createStage(Parent root, String title) {
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.setTitle(title);
        return stage;
    }

    private void closeCurrentStage(MouseEvent event) {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
    }

    public void initializeWithResult(AnalysisResult result) {
        this.currentResult = result;
        updateWordsLabel();
    }

    private void updateWordsLabel() {
        Map<String, Set<String>> wordsByType = currentResult.getWordsByType();
        String joinedWords = Stream.of(NOUN, VERB, ADJ)
                .filter(wordsByType::containsKey)
                .flatMap(type -> wordsByType.get(type).stream())
                .collect(Collectors.joining(", "));

        wordsLabel.setText(joinedWords);
    }

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

    private void validateInputs() {
        if (number.getValue() <= 0) {
            throw new IllegalArgumentException("The number of sentences must be greater than 0.");
        }

        String template = templates.getValue();
        if (template == null || template.isEmpty() || template.equals(DEFAULT_TEMPLATE)) {
            throw new IllegalArgumentException("Select a valid template.");
        }
    }

    private List<String> generateSentences() {
        Map<String, Set<String>> wordsByType = currentResult.getWordsByType();

        List<String> nouns = new ArrayList<>(wordsByType.getOrDefault(NOUN, Collections.emptySet()));
        List<String> verbs = new ArrayList<>(wordsByType.getOrDefault(VERB, Collections.emptySet()));
        List<String> adjectives = new ArrayList<>(wordsByType.getOrDefault(ADJ, Collections.emptySet()));

        try {
            return new SentenceGenerator().generateSentences(
                    numberValue, selectedTemplate, nouns, verbs, adjectives);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void navigateToResultsView(MouseEvent event, List<String> sentences) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(RESULTS_VIEW));
        Parent root = loader.load();

        ResultsController controller = loader.getController();
        configureResultsController(controller, sentences);

        Stage stage = createStage(root, "Results");
        stage.show();

        closeCurrentStage(event);
    }

    private void configureResultsController(ResultsController controller, List<String> sentences) {
        controller.setPreviousView(getPreviousViewType());
        controller.setAnalysisResult(currentResult);
        controller.showGeneratedSentences(sentences);
        controller.setNumberValue(numberValue);
        controller.setSelectedTemplate(selectedTemplate);
    }

    protected abstract String getPreviousViewType();

    public void setNumberValue(int value) {
        this.numberValue = value;
        if (number != null && number.getValueFactory() != null) {
            number.getValueFactory().setValue(value);
        }
    }

    public void setSelectedTemplate(String template) {
        this.selectedTemplate = template;
        if (templates != null) {
            templates.setValue(template);
        }
    }
}