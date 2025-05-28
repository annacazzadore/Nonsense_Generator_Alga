package it.unipd.nonsense.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

public class MainControllerTest extends ApplicationTest {

    private MainController controller;
    private TextField sentenceField;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unipd/nonsense/views/main.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        stage.setScene(new Scene(root));
        stage.show();

        // lookup del campo sentence dopo il rendering
        sentenceField = lookup("#sentence").query();
    }

    @BeforeEach
    void setUp() {
        // Non necessario qui, lo start() lo gestisce
    }

    @Test
    void testSetSentenceTextUpdatesField() {
        interact(() -> controller.setSentenceText("Frase di test"));
        assertEquals("Frase di test", sentenceField.getText());
    }

    @Test
    void testResetClearsSentenceField() {
        interact(() -> controller.setSentenceText("Frase di test"));
        interact(controller::reset);
        assertEquals("", sentenceField.getText());
    }

    @Test
    void testGetSentenceTextReturnsCorrectValue() {
        interact(() -> sentenceField.setText("Contenuto inserito"));
        assertEquals("Contenuto inserito", controller.getSentenceText());
    }
}
