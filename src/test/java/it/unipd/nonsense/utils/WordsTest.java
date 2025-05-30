package it.unipd.nonsense.utils;

import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the abstract {@link it.unipd.nonsense.utils.Words} base class.
 * This class verifies common dictionary functionality including:
 * <ul>
 *   <li>Dictionary loading from files</li>
 *   <li>Random word generation</li>
 *   <li>Edge case handling</li>
 *   <li>Error conditions</li>
 * </ul>
 *
 * <p>The class uses a test subclass of {@code Words} to test the abstract base class functionality.
 * It creates temporary dictionary files for testing and cleans them up after tests complete.</p>
 *
 * @see it.unipd.nonsense.utils.Words
 */
public class WordsTest {

    /**
     * Temporary dictionary file path containing sample words for testing.
     */
    private static Path tempDictionary;

    /**
     * Temporary empty dictionary file path for testing edge cases.
     */
    private static Path emptyTempDictionary;

    /**
     * Test implementation of the abstract {@link Words} class.
     * This concrete subclass allows testing of the base class functionality.
     */
    static class TestWords extends Words {
        /**
         * Creates a new TestWords instance with the specified dictionary file.
         *
         * @param dictionaryPath path to the dictionary file
         * @throws IOException if an I/O error occurs reading the dictionary
         */
        public TestWords(String dictionaryPath) throws IOException {
            super(dictionaryPath);
        }
    }

    /**
     * Creates temporary dictionary files before all tests run.
     *
     * @throws IOException if an I/O error occurs while creating temporary files
     */
    @BeforeAll
    static void createTempFiles() throws IOException {
        // Create a dictionary with sample words
        tempDictionary = Files.createTempFile("test-dictionary", ".txt");
        List<String> sampleWords = List.of("apple", "banana", "cherry", "date", "elderberry");
        Files.write(tempDictionary, sampleWords);

        // Create an empty dictionary file
        emptyTempDictionary = Files.createTempFile("empty-dictionary", ".txt");
    }

    /**
     * Cleans up temporary files after all tests complete.
     *
     * @throws IOException if an I/O error occurs while deleting temporary files
     */
    @AfterAll
    static void cleanup() throws IOException {
        Files.deleteIfExists(tempDictionary);
        Files.deleteIfExists(emptyTempDictionary);
    }

    /* Dictionary loading tests */

    /**
     * Tests loading words from a valid dictionary file.
     * Verifies that:
     * <ul>
     *   <li>The words list is properly initialized</li>
     *   <li>The correct number of words is loaded</li>
     * </ul>
     *
     * @throws IOException if an I/O error occurs
     */
    @Test
    public void testLoadWordsFromFile() throws IOException {
        TestWords words = new TestWords(tempDictionary.toString());
        assertNotNull(words.words, "Words list should be initialized");
        assertEquals(5, words.words.size(), "Expected 5 words in dictionary");
    }

    /**
     * Tests loading an empty dictionary file.
     * Verifies that the dictionary is properly initialized as empty.
     *
     * @throws IOException if an I/O error occurs
     */
    @Test
    public void testLoadEmptyDictionary() throws IOException {
        TestWords words = new TestWords(emptyTempDictionary.toString());
        assertTrue(words.words.isEmpty(), "Dictionary should be empty");
    }

    /**
     * Tests that the dictionary doesn't contain duplicate words.
     * Verifies that all loaded words are unique.
     *
     * @throws IOException if an I/O error occurs
     */
    @Test
    public void testDictionaryHasNoDuplicates() throws IOException {
        TestWords words = new TestWords(tempDictionary.toString());
        Set<String> uniqueWords = new HashSet<>(words.words);
        assertEquals(words.words.size(), uniqueWords.size(),
                "Dictionary should not contain duplicate words");
    }

    /**
     * Tests behavior with an invalid dictionary path.
     * Verifies that an IOException is thrown when the dictionary file doesn't exist.
     */
    @Test
    public void testInvalidDictionaryPathThrowsException() {
        assertThrows(IOException.class, () -> new TestWords("/nonexistent.txt"));
    }

    /* Single word retrieval tests */

    /**
     * Tests getting a single random word from dictionary.
     * Verifies that the returned word exists in the dictionary.
     *
     * @throws IOException if an I/O error occurs
     */
    @Test
    public void testGetRandomReturnsWord() throws IOException {
        TestWords words = new TestWords(tempDictionary.toString());
        String word = words.getRandom();
        assertTrue(words.words.contains(word),
                "Random word should be in the list");
    }

    /**
     * Tests getting random word from empty dictionary.
     * Verifies that NoSuchElementException is thrown when the dictionary is empty.
     *
     * @throws IOException if an I/O error occurs
     */
    @Test
    public void testGetRandomFromEmptyDictionary() throws IOException {
        TestWords words = new TestWords(emptyTempDictionary.toString());
        assertThrows(NoSuchElementException.class, words::getRandom);
    }

    /* Multiple words retrieval tests */

    /**
     * Tests getting multiple random words from dictionary.
     * Verifies:
     * <ul>
     *   <li>The correct number of words is returned</li>
     *   <li>All returned words are from the dictionary</li>
     * </ul>
     *
     * @throws IOException if an I/O error occurs
     */
    @Test
    public void testGetRandomFromDictionary() throws IOException {
        TestWords words = new TestWords(tempDictionary.toString());
        List<String> randomWords = words.getRandomFromDictionary(3);
        assertEquals(3, randomWords.size(), "Should return exactly 3 words");
        assertTrue(words.words.containsAll(randomWords),
                "All returned words should be from dictionary");
    }

    /**
     * Tests the edge case of requesting zero random words.
     * Verifies that an empty list is returned when the count is 0.
     *
     * @throws IOException if an I/O error occurs
     */
    @Test
    public void testGetRandomFromDictionaryWithZeroCount() throws IOException {
        TestWords words = new TestWords(tempDictionary.toString());
        List<String> randomWords = words.getRandomFromDictionary(0);
        assertTrue(randomWords.isEmpty(),
                "Should return empty list when count is 0");
    }
}