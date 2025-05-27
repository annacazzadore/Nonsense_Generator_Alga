package it.unipd.nonsense.controller;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MainControllerTest {

    @Test
    void testControllerInitialState() {
        MainController controller = new MainController();
        assertNotNull(controller);
    }
}