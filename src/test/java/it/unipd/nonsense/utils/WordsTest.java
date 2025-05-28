package it.unipd.nonsense.utils;

import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the abstract {@link Words} base class.
 * Verifies common dictionary functionality including:
 * - Dictionary loading from files
 * - Random word generation
 * - Edge case handling
 * - Error conditions
 *
 * Uses a test subclass of Words to test the abstract base class functionality.
 */
public class WordsTest {

    // Temporary files for testing
    private static Path tempDictionary;
    private static Path emptyTempDictionary;

    /**
     * Test implementation of the abstract Words class.
     * Allows testing of the base class functionality.
     */
    static class TestWords extends Words {
        public TestWords(String dictionaryPath) throws IOException {
            super(dictionaryPath);
        }
    }

    /**
     * Creates temporary dictionary files before all tests run.
     * - One with sample words
     * - One empty dictionary
     *
     * @throws IOException if file creation fails
     */
    @BeforeAll
    static void createTempFiles() throws IOException {
        // Create dictionary with sample words
        tempDictionary = Files.createTempFile("test-dictionary", ".txt");
        List<String> sampleWords = List.of("apple", "banana", "cherry", "date", "elderberry");
        Files.write(tempDictionary, sampleWords);

        // Create empty dictionary
        emptyTempDictionary = Files.createTempFile("empty-dictionary", ".txt");
        // Intentionally left empty
    }

    /**
     * Cleans up temporary files after all tests complete.
     *
     * @throws IOException if file deletion fails
     */
    @AfterAll
    static void cleanup() throws IOException {
        Files.deleteIfExists(tempDictionary);
        Files.deleteIfExists(emptyTempDictionary);
    }

    /**
     * Tests loading words from a valid dictionary file.
     * Verifies:
     * - Words list is initialized
     * - Correct number of words loaded
     *
     * @throws IOException if file reading fails
     */
    @Test
    public void testLoadWordsFromFile() throws IOException {
        TestWords words = new TestWords(tempDictionary.toString());
        assertNotNull(words.words, "Words list should be initialized");
        assertEquals(5, words.words.size(), "Expected 5 words in dictionary");
    }

    /**
     * Tests loading an empty dictionary file.
     * Verifies the words list is empty but initialized.
     *
     * @throws IOException if file reading fails
     */
    @Test
    public void testLoadEmptyDictionary() throws IOException {
        TestWords words = new TestWords(emptyTempDictionary.toString());
        assertTrue(words.words.isEmpty(), "Dictionary should be empty");
    }

    /**
     * Tests getting a single random word from dictionary.
     * Verifies the returned word exists in the dictionary.
     *
     * @throws IOException if file reading fails
     */
    @Test
    public void testGetRandomReturnsWord() throws IOException {
        TestWords words = new TestWords(tempDictionary.toString());
        String word = words.getRandom();
        assertTrue(words.words.contains(word), "Random word should be in the list");
    }

    /**
     * Tests getting multiple random words from dictionary.
     * Verifies:
     * - Correct number of words returned
     * - All words are from the dictionary
     *
     * @throws IOException if file reading fails
     */
    @Test
    public void testGetRandomFromDictionary() throws IOException {
        TestWords words = new TestWords(tempDictionary.toString());
        List<String> randomWords = words.getRandomFromDictionary(3);
        assertEquals(3, randomWords.size(), "Should return exactly 3 words");
        assertTrue(words.words.containsAll(randomWords), "All returned words should be from dictionary");
    }

    /**
     * Tests getting random word from empty dictionary.
     * Verifies NoSuchElementException is thrown.
     *
     * @throws IOException if file reading fails
     */
    @Test
    public void testGetRandomFromEmptyDictionary() throws IOException {
        TestWords words = new TestWords(emptyTempDictionary.toString());
        assertThrows(NoSuchElementException.class, words::getRandom);
    }

    /**
     * Tests edge case of requesting zero random words.
     * Verifies empty list is returned.
     *
     * @throws IOException if file reading fails
     */
    @Test
    public void testGetRandomFromDictionaryWithZeroCount() throws IOException {
        TestWords words = new TestWords(tempDictionary.toString());
        List<String> randomWords = words.getRandomFromDictionary(0);
        assertTrue(randomWords.isEmpty(), "Should return empty list when count is 0");
    }

    /**
     * Tests behavior with invalid dictionary path.
     * Verifies IOException is thrown.
     */
    @Test
    public void testInvalidDictionaryPathThrowsException() {
        assertThrows(IOException.class, () -> new TestWords("/nonexistent.txt"));
    }
}