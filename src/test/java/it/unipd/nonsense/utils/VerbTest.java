package it.unipd.nonsense.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the {@link Verb} dictionary.
 * Verifies that the default constructor correctly loads verbs from the predefined path.
 *
 * <p>Like other word-type tests, this class only checks the default path initialization,
 * since {@link Verb} inherits core functionality from {@link Words}.
 */
public class VerbTest {
    /**
     * Tests that the default constructor of {@link Verb} loads verbs
     * without throwing exceptions.
     *
     * <p>This confirms the default verb dictionary path is correctly configured.
     */
    @Test
    public void testDefaultConstructorLoadsVerbs() {
        assertDoesNotThrow(() -> new Verb());
    }
}