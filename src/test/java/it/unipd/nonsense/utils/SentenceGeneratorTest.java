package it.unipd.nonsense.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test class for {@link SentenceGenerator} that verifies all aspects
 * of sentence generation functionality including template processing, word substitution,
 * default dictionary usage, and error handling.
 *
 * <p>The tests are organized into logical groups:
 * <ul>
 *   <li>Positive test cases for normal operation</li>
 *   <li>File operation tests for persistence functionality</li>
 *   <li>Negative test cases for error handling</li>
 * </ul>
 */
public class SentenceGeneratorTest {

    /** The SentenceGenerator instance under test */
    private SentenceGenerator generator;

    /**
     * Initializes a fresh SentenceGenerator instance before each test method execution.
     *
     * @throws IOException if there's an error loading default dictionaries
     */
    @BeforeEach
    public void setUp() throws IOException {
        generator = new SentenceGenerator();
    }

    /* POSITIVE TEST CASES */

    /**
     * Tests sentence generation with custom word lists.
     * Verifies that:
     * <ul>
     *   <li>The correct number of sentences is generated</li>
     *   <li>All placeholders are properly replaced</li>
     *   <li>Generated sentences match the expected pattern</li>
     * </ul>
     *
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
     * Tests sentence generation with null word lists, which should trigger
     * the use of default dictionaries.
     *
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
     * Tests sentence generation with empty word lists, which should trigger
     * the use of default dictionaries.
     *
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
     * Tests complex templates with multiple placeholders of the same type.
     * Verifies that all instances of each placeholder type are properly replaced.
     *
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
            assertTrue(sentence.matches("\\w+ \\w+ \\w+ and \\w+ \\w+ \\w+\\."));
        }
    }

    /**
     * Tests generation of exactly one sentence when count parameter is 1.
     * Verifies both the quantity and exact expected output.
     *
     * @throws IOException if dictionary operations fail
     */
    @Test
    public void testGenerateSentences_SingleSentence_WhenCountIsOne() throws IOException {
        String template = "The [adjective] [noun] [verb].";
        List<String> nouns = List.of("cat");
        List<String> verbs = List.of("jumps");
        List<String> adjectives = List.of("quick");

        List<String> sentences = generator.generateSentences(1, template, nouns, verbs, adjectives);

        assertEquals(1, sentences.size());
        String result = sentences.getFirst();
        assertFalse(result.contains("[adjective]") || result.contains("[noun]") || result.contains("[verb]"));
        assertEquals("The quick cat jumps.", result);
    }

    /**
     * Tests complex template with multiple occurrences of each placeholder type.
     * Verifies:
     * <ul>
     *   <li>All placeholders are replaced</li>
     *   <li>Correct number of each word type appears</li>
     *   <li>All words come from expected sources</li>
     * </ul>
     *
     * @throws IOException if dictionary operations fail
     */
    @Test
    public void testReplacePlaceholders_ComplexTemplate() throws IOException {
        String template = "The [adjective] [noun] [verb] over the [adjective] [noun], and then [verb] a [noun].";
        List<String> nouns = Arrays.asList("cat", "dog", "house");
        List<String> verbs = Arrays.asList("jumps", "runs", "sings");
        List<String> adjectives = Arrays.asList("quick", "lazy", "bright");

        List<String> sentences = generator.generateSentences(1, template, nouns, verbs, adjectives);
        String result = sentences.getFirst();

        assertFalse(
                result.contains("[noun]") || result.contains("[verb]") || result.contains("[adjective]"),
                "Sentence contains unreplaced placeholders"
        );

        String[] words = result.split("\\s+");
        for (String word : words) {
            String cleanedWord = word.replaceAll("[^a-zA-Z]", "");
            if (nouns.contains(cleanedWord)) continue;
            if (verbs.contains(cleanedWord)) continue;
            if (adjectives.contains(cleanedWord)) continue;
            if (Arrays.asList("The", "over", "the", "and", "then", "a").contains(word)) continue;
            fail("Unrecognized word: " + word);
        }

        long nounCount = nouns.stream().filter(noun -> result.matches(".*\\b" + noun + "\\b.*")).count();
        long verbCount = verbs.stream().filter(verb -> result.matches(".*\\b" + verb + "\\b.*")).count();
        long adjCount = adjectives.stream().filter(adj -> result.matches(".*\\b" + adj + "\\b.*")).count();

        assertEquals(3, nounCount);
        assertEquals(2, verbCount);
        assertEquals(2, adjCount);
    }

    /* FILE OPERATIONS TESTS */

    /**
     * Tests saving generated sentences to a text file.
     * Verifies:
     * <ul>
     *   <li>File is created successfully</li>
     *   <li>File contains all generated sentences</li>
     *   <li>Each sentence is properly written in its own line</li>
     * </ul>
     *
     * @throws IOException if file operations fail
     */
    @Test
    public void testSaveSentencesToFile() throws IOException {
        String template = "The [noun] [verb].";
        List<String> sentences = generator.generateSentences(3, template, null, null, null);

        Path tempFile = Files.createTempFile("generated-sentences", ".txt");
        try {
            Files.write(tempFile, sentences);
            List<String> fileContent = Files.readAllLines(tempFile);
            assertEquals(sentences.size(), fileContent.size());
            for (int i = 0; i < sentences.size(); i++) {
                assertEquals(sentences.get(i), fileContent.get(i));
            }
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    /* NEGATIVE TEST CASES */

    /**
     * Tests behavior when invalid sentence count (0) is provided.
     * Verifies that:
     * <ul>
     *   <li>An IllegalArgumentException is thrown</li>
     *   <li>The exception message is correct</li>
     * </ul>
     */
    @Test
    public void testGenerateSentences_InvalidCount_ThrowsException() {
        String template = "I see a [noun].";
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                generator.generateSentences(0, template, null, null, null));
        assertEquals("The number of sentences must be greater than 0", ex.getMessage());
    }

    /**
     * Tests behavior with various invalid template formats.
     * Verifies proper exception throwing for:
     * <ul>
     *   <li>Dash-only template</li>
     *   <li>Empty string template</li>
     *   <li>Null template</li>
     * </ul>
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