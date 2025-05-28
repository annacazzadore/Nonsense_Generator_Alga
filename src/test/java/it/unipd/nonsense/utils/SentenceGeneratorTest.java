package it.unipd.nonsense.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SentenceGenerator}.
 * Verifies the functionality of sentence generation including:
 * - Template processing with placeholders
 * - Word substitution from custom lists
 * - Default dictionary usage
 * - Error handling for invalid inputs
 */
public class SentenceGeneratorTest {

    private SentenceGenerator generator;

    /**
     * Initializes a fresh SentenceGenerator instance before each test.
     * @throws IOException if dictionary loading fails
     */
    @BeforeEach
    public void setUp() throws IOException {
        generator = new SentenceGenerator();
    }

    /**
     * Tests sentence generation with custom word lists.
     * Verifies:
     * - Correct number of sentences generated
     * - All placeholders are properly replaced
     * - Generated sentences match expected pattern
     * @throws IOException if dictionary operations fail
     */
    @Test
    public void testGenerateSentences_WithCustomWords() throws IOException {
        String template = "The [adjective] [noun] likes to [verb].";
        List<String> nouns = List.of("cat", "dog");
        List<String> verbs = List.of("jump", "run");
        List<String> adjectives = List.of("happy", "sad");

        List<String> sentences = generator.generateSentences(3, template, nouns, verbs, adjectives);

        assertNotNull(sentences);
        assertEquals(3, sentences.size());
        for (String sentence : sentences) {
            assertFalse(sentence.contains("[noun]"));
            assertFalse(sentence.contains("[verb]"));
            assertFalse(sentence.contains("[adjective]"));
            assertTrue(sentence.matches("The \\w+ \\w+ likes to \\w+\\."));
        }
    }

    /**
     * Tests sentence generation with null word lists (should use default dictionaries).
     * Verifies:
     * - Proper handling of null inputs
     * - Placeholders still get replaced
     * - Sentences maintain structure
     * @throws IOException if dictionary operations fail
     */
    @Test
    public void testGenerateSentences_WithNullLists() throws IOException {
        String template = "A [adjective] [noun] will [verb] soon.";

        List<String> sentences = generator.generateSentences(2, template, null, null, null);

        assertNotNull(sentences);
        assertEquals(2, sentences.size());
        for (String sentence : sentences) {
            assertFalse(sentence.contains("[noun]"));
            assertFalse(sentence.contains("[verb]"));
            assertFalse(sentence.contains("[adjective]"));
        }
    }

    /**
     * Tests sentence generation with empty word lists (should use default dictionaries).
     * Verifies:
     * - Proper handling of empty collections
     * - Placeholders still get replaced
     * - Single sentence generation
     * @throws IOException if dictionary operations fail
     */
    @Test
    public void testGenerateSentences_WithEmptyLists() throws IOException {
        String template = "The [noun] [verb].";
        List<String> nouns = new ArrayList<>();
        List<String> verbs = new ArrayList<>();

        List<String> sentences = generator.generateSentences(1, template, nouns, verbs, null);

        assertNotNull(sentences);
        assertEquals(1, sentences.size());
        assertFalse(sentences.getFirst().contains("[noun]"));
        assertFalse(sentences.getFirst().contains("[verb]"));
    }

    /**
     * Tests complex templates with multiple placeholders.
     * Verifies:
     * - Correct handling of duplicate placeholders
     * - Proper sentence structure
     * - All placeholders replaced
     * @throws IOException if dictionary operations fail
     */
    @Test
    public void testGenerateSentences_WithMultiplePlaceholders() throws IOException {
        String template = "[noun] [verb] [noun] and [verb] [adjective] [noun].";
        List<String> nouns = List.of("cat", "dog", "bird");
        List<String> verbs = List.of("chases", "eats", "plays");
        List<String> adjectives = List.of("happy", "small");

        List<String> sentences = generator.generateSentences(2, template, nouns, verbs, adjectives);

        assertNotNull(sentences);
        assertEquals(2, sentences.size());
        for (String sentence : sentences) {
            assertFalse(sentence.contains("[noun]"));
            assertFalse(sentence.contains("[verb]"));
            assertFalse(sentence.contains("[adjective]"));
            // Verify the structure of the sentence
            assertTrue(sentence.matches("\\w+ \\w+ \\w+ and \\w+ \\w+ \\w+\\."));
        }
    }

    /**
     * Tests invalid sentence count parameter.
     * Verifies:
     * - Proper exception thrown for zero count
     * - Correct exception message
     */
    @Test
    public void testGenerateSentences_InvalidCount_ThrowsException() {
        String template = "I see a [noun].";
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                generator.generateSentences(0, template, null, null, null));
        assertEquals("The number of sentences must be greater than 0", ex.getMessage());
    }

    /**
     * Tests invalid template formats.
     * Verifies:
     * - Exception thrown for invalid templates
     * - Different invalid cases (dash, empty, null)
     * - Consistent error message
     */
    @Test
    public void testGenerateSentences_InvalidTemplate_ThrowsException() {
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () ->
                generator.generateSentences(1, "-", null, null, null));
        assertEquals("Select a valid template", ex1.getMessage());

        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () ->
                generator.generateSentences(1, "", null, null, null));
        assertEquals("Select a valid template", ex2.getMessage());

        IllegalArgumentException ex3 = assertThrows(IllegalArgumentException.class, () ->
                generator.generateSentences(1, null, null, null, null));
        assertEquals("Select a valid template", ex3.getMessage());
    }
}