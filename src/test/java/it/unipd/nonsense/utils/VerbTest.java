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
 * Test class for {@link Verb} dictionary functionality.
 * This class verifies the behavior of the Verb class including:
 * - Dictionary loading from files
 * - Random verb generation
 * - Error handling for file operations
 *
 * Uses temporary files for isolated testing to avoid modifying production resources.
 */
public class VerbTest {
    // Temporary file path for testing verb dictionary
    private static Path tempVerbDictionary;

    /**
     * Sets up test environment before all tests run.
     * Creates a temporary verb dictionary file with sample data.
     *
     * @throws IOException if temporary file creation fails
     */
    @BeforeAll
    static void setup() throws IOException {
        // Create a temporary verb dictionary file with test data
        tempVerbDictionary = Files.createTempFile("test-verbs", ".txt");
        List<String> sampleVerbs = List.of("run", "jump", "swim", "think", "write");
        Files.write(tempVerbDictionary, sampleVerbs);
    }

    /**
     * Cleans up test environment after all tests complete.
     * Deletes the temporary verb dictionary file.
     *
     * @throws IOException if file deletion fails
     */
    @AfterAll
    static void cleanup() throws IOException {
        // Delete the temporary test file
        Files.deleteIfExists(tempVerbDictionary);
    }

    /**
     * Tests that the verb dictionary loads successfully from a file.
     * Verifies the internal words list is properly initialized and not empty.
     *
     * @throws IOException if file reading fails
     */
    @Test
    public void testVerbDictionaryLoadsSuccessfully() throws IOException {
        Verb verb = new Verb(tempVerbDictionary.toString());
        assertNotNull(verb.words, "Verb list should not be null");
        assertFalse(verb.words.isEmpty(), "Verb list should not be empty");
    }

    /**
     * Tests that the verb dictionary doesn't contain duplicates.
     * Verifies all verbs in the loaded dictionary are unique.
     *
     * @throws IOException if file reading fails
     */
    @Test
    public void testVerbDictionaryHasNoDuplicates() throws IOException {
        Verb verb = new Verb(tempVerbDictionary.toString());
        Set<String> uniqueVerbs = new HashSet<>(verb.words);
        assertEquals(verb.words.size(), uniqueVerbs.size(), "Verb dictionary should not contain duplicates");
    }

    /**
     * Tests that getRandom() returns a valid verb from the dictionary.
     * Verifies the returned word exists in the loaded dictionary.
     *
     * @throws IOException if file reading fails
     */
    @Test
    public void testGetRandomReturnsValidVerb() throws IOException {
        Verb verb = new Verb(tempVerbDictionary.toString());
        String word = verb.getRandom();
        assertTrue(verb.words.contains(word), "Returned word should be in the verb list");
    }

    /**
     * Tests retrieval of multiple random verbs.
     * Verifies:
     * - Correct number of verbs returned
     * - All returned verbs exist in the dictionary
     *
     * @throws IOException if file reading fails
     */
    @Test
    public void testGetMultipleRandomVerbs() throws IOException {
        Verb verb = new Verb(tempVerbDictionary.toString());
        List<String> randomVerbs = verb.getRandomFromDictionary(3);
        assertEquals(3, randomVerbs.size(), "Should return exactly 3 random verbs");
        assertTrue(verb.words.containsAll(randomVerbs), "All returned verbs should be from the verb list");
    }

    /**
     * Tests edge case of requesting zero random verbs.
     * Verifies an empty list is returned when requesting 0 verbs.
     *
     * @throws IOException if file reading fails
     */
    @Test
    public void testGetZeroRandomVerbs() throws IOException {
        Verb verb = new Verb(tempVerbDictionary.toString());
        List<String> randomVerbs = verb.getRandomFromDictionary(0);
        assertTrue(randomVerbs.isEmpty(), "Should return empty list when requesting 0 verbs");
    }

    /**
     * Tests error handling when dictionary file doesn't exist.
     * Verifies the class throws IOException for invalid paths.
     */
    @Test
    public void testVerbDictionaryThrowsExceptionWhenFileNotFound() {
        assertThrows(IOException.class, () -> new Verb("/nonexistent/path/verbs.txt"));
    }

    /**
     * Tests the default constructor loads verbs successfully.
     * Verifies no exception is thrown when using default dictionary path.
     * Note: Assumes a valid default dictionary exists at the expected location.
     */
    @Test
    public void testDefaultConstructorLoadsVerbs() {
        assertDoesNotThrow(() -> new Verb());
    }
}