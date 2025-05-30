package it.unipd.nonsense.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Noun} dictionary functionality.
 * This class verifies the behavior of the Noun class including:
 * - Dictionary loading from files
 * - Random noun generation
 * - Error handling for file operations
 *
 * Uses temporary files for isolated testing to avoid modifying production resources.
 */
public class NounTest {
    // Temporary file path for testing
    private static Path tempNounDictionary;

    /**
     * Sets up test environment before all tests run.
     * Creates a temporary noun dictionary file with sample data.
     *
     * @throws IOException if temporary file creation fails
     */
    @BeforeAll
    static void setup() throws IOException {
        // Create a temporary noun dictionary for testing
        tempNounDictionary = Files.createTempFile("test-nouns", ".txt");
        List<String> sampleNouns = List.of("apple", "book", "car", "dog", "house");
        Files.write(tempNounDictionary, sampleNouns);
    }

    /**
     * Cleans up test environment after all tests complete.
     * Deletes the temporary noun dictionary file.
     *
     * @throws IOException if file deletion fails
     */
    @AfterAll
    static void cleanup() throws IOException {
        // Clean up the temporary file
        Files.deleteIfExists(tempNounDictionary);
    }

    /**
     * Tests that the noun dictionary loads successfully from a file.
     * Verifies the internal words list is properly initialized.
     *
     * @throws IOException if file reading fails
     */
    @Test
    public void testNounDictionaryLoadsSuccessfully() throws IOException {
        Noun noun = new Noun(tempNounDictionary.toString());
        assertNotNull(noun.words, "Nouns list should not be null");
        assertFalse(noun.words.isEmpty(), "Nouns list should not be empty");
    }

    /**
     * Tests that the noun dictionary doesn't contain duplicates.
     * Verifies all nouns in the loaded dictionary are unique.
     *
     * @throws IOException if file reading fails
     */
    @Test
    public void testNounDictionaryHasNoDuplicates() throws IOException {
        Noun noun = new Noun(tempNounDictionary.toString());
        Set<String> uniqueNouns = new HashSet<>(noun.words);
        assertEquals(noun.words.size(), uniqueNouns.size(), "Noun dictionary should not contain duplicates");
    }

    /**
     * Tests that getRandom() returns a valid noun from the dictionary.
     * Verifies the returned word exists in the loaded dictionary.
     *
     * @throws IOException if file reading fails
     */
    @Test
    public void testGetRandomReturnsValidNoun() throws IOException {
        Noun noun = new Noun(tempNounDictionary.toString());
        String word = noun.getRandom();
        assertTrue(noun.words.contains(word), "Returned word should be in the noun list");
    }

    /**
     * Tests retrieval of multiple random nouns.
     * Verifies:
     * - Correct number of nouns returned
     * - All returned nouns exist in the dictionary
     *
     * @throws IOException if file reading fails
     */
    @Test
    public void testGetMultipleRandomNouns() throws IOException {
        Noun noun = new Noun(tempNounDictionary.toString());
        List<String> randomNouns = noun.getRandomFromDictionary(3);
        assertEquals(3, randomNouns.size(), "Should return exactly 3 random nouns");
        assertTrue(noun.words.containsAll(randomNouns), "All returned nouns should be from the noun list");
    }

    /**
     * Tests edge case of requesting zero random nouns.
     * Verifies an empty list is returned.
     *
     * @throws IOException if file reading fails
     */
    @Test
    public void testGetZeroRandomNouns() throws IOException {
        Noun noun = new Noun(tempNounDictionary.toString());
        List<String> randomNouns = noun.getRandomFromDictionary(0);
        assertTrue(randomNouns.isEmpty(), "Should return empty list when requesting 0 nouns");
    }

    /**
     * Tests error handling when dictionary file doesn't exist.
     * Verifies the class throws IOException for invalid paths.
     */
    @Test
    public void testNounDictionaryThrowsExceptionWhenFileNotFound() {
        assertThrows(IOException.class, () -> new Noun("/nonexistent/path/nouns.txt"));
    }

    /**
     * Tests the default constructor loads nouns successfully.
     * Verifies no exception is thrown when using default dictionary path.
     */
    @Test
    public void testDefaultConstructorLoadsNouns() {
        // This assumes the real dictionary file exists at the default location
        assertDoesNotThrow(() -> new Noun());
    }
}