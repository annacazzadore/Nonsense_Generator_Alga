package it.unipd.nonsense.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InputHandlerTest {
    private InputHandler inputHandler;

    @BeforeEach
    void setUp() {
        inputHandler = new InputHandler();
    }

    @Test
    void testInputIsStoredCorrectly() {
        inputHandler.setInput("Ciao mondo");
        assertEquals("Ciao mondo", inputHandler.getInput());
    }
}