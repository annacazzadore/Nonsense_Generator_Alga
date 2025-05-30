package it.unipd.nonsense.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the {@link Noun} dictionary.
 * Verifies that the default constructor correctly loads nouns from the predefined path.
 *
 * <p>This test focuses on the minimal behavior specific to {@link Noun}, as most functionality
 * is inherited from {@link Words} and tested in {@link WordsTest}.
 */
public class NounTest {
    /**
     * Tests that the default constructor of {@link Noun} loads nouns
     * without throwing exceptions.
     *
     * <p>This ensures the default dictionary path for nouns is valid.
     */
    @Test
    public void testDefaultConstructorLoadsNouns() {
        assertDoesNotThrow(() -> new Noun());
    }
}