package it.unipd.nonsense.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the {@link Adjective} dictionary.
 * Verifies that the default constructor correctly loads adjectives from the predefined path.
 *
 * <p>This test ensures the {@link Adjective} class properly initializes with its default
 * dictionary path, as no additional logic is introduced beyond the base {@link Words} class.
 */
public class AdjectiveTest {
    /**
     * Tests that the default constructor of {@link Adjective} loads adjectives
     * without throwing exceptions.
     *
     * <p>This validates that the default dictionary path is correct and accessible.
     */
    @Test
    public void testDefaultConstructorLoadsAdjectives() {
        assertDoesNotThrow(() -> new Adjective());
    }
}