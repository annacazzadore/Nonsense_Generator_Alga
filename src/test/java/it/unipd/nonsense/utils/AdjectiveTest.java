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
 * Test class for the Adjective utility class.
 * Verifies the functionality of loading and retrieving adjectives from a dictionary file.
 */
public class AdjectiveTest {
    // Temporary file path for the test adjective dictionary
    private static Path tempAdjDictionary;

    /**
     * Setup method executed once before all tests.
     * Creates a temporary file with sample adjectives for testing purposes.
     *
     * @throws IOException if there's an error creating or writing to the temporary file
     */
    @BeforeAll
    static void setup() throws IOException {
        // Create a temporary adjective dictionary for testing
        tempAdjDictionary = Files.createTempFile("test-adjectives", ".txt");
        List<String> sampleAdjectives = List.of("happy", "blue", "quick", "loud", "bright");
        Files.write(tempAdjDictionary, sampleAdjectives);
    }

    /**
     * Cleanup method executed once after all tests.
     * Deletes the temporary file created for testing.
     *
     * @throws IOException if there's an error deleting the temporary file
     */
    @AfterAll
    static void cleanup() throws IOException {
        // Clean up the temporary file
        Files.deleteIfExists(tempAdjDictionary);
    }

    /**
     * Tests that the adjective dictionary loads successfully from a file.
     *
     * @throws IOException if there's an error reading the dictionary file
     */
    @Test
    public void testAdjectiveDictionaryLoadsSuccessfully() throws IOException {
        Adjective adj = new Adjective(tempAdjDictionary.toString());
        assertNotNull(adj.words, "Adjectives list should not be null");
        assertFalse(adj.words.isEmpty(), "Adjectives list should not be empty");
    }

    /**
     * Tests that the adjective dictionary doesn't contain duplicates.
     * Verifies all adjectives in the loaded dictionary are unique.
     *
     * @throws IOException if file reading fails
     */
    @Test
    public void testAdjectiveDictionaryHasNoDuplicates() throws IOException {
        Adjective adj = new Adjective(tempAdjDictionary.toString());
        Set<String> uniqueAdjectives = new HashSet<>(adj.words);
        assertEquals(adj.words.size(), uniqueAdjectives.size(), "Adjective dictionary should not contain duplicates");
    }

    /**
     * Tests that getRandom() returns a valid adjective from the dictionary.
     *
     * @throws IOException if there's an error reading the dictionary file
     */
    @Test
    public void testGetRandomReturnsValidAdjective() throws IOException {
        Adjective adj = new Adjective(tempAdjDictionary.toString());
        String word = adj.getRandom();
        assertTrue(adj.words.contains(word), "Returned word should be in the adjective list");
    }

    /**
     * Tests that getRandomFromDictionary() returns the correct number of adjectives
     * and that all returned words are from the dictionary.
     *
     * @throws IOException if there's an error reading the dictionary file
     */
    @Test
    public void testGetMultipleRandomAdjectives() throws IOException {
        Adjective adj = new Adjective(tempAdjDictionary.toString());
        List<String> randomAdjectives = adj.getRandomFromDictionary(3);
        assertEquals(3, randomAdjectives.size(), "Should return exactly 3 random adjectives");
        assertTrue(adj.words.containsAll(randomAdjectives), "All returned adjectives should be from the adjective list");
    }

    /**
     * Tests that getRandomFromDictionary() returns an empty list when requesting 0 adjectives.
     *
     * @throws IOException if there's an error reading the dictionary file
     */
    @Test
    public void testGetZeroRandomAdjectives() throws IOException {
        Adjective adj = new Adjective(tempAdjDictionary.toString());
        List<String> randomAdjectives = adj.getRandomFromDictionary(0);
        assertTrue(randomAdjectives.isEmpty(), "Should return empty list when requesting 0 adjectives");
    }

    /**
     * Tests that the constructor throws an exception when the dictionary file doesn't exist.
     */
    @Test
    public void testAdjectiveDictionaryThrowsExceptionWhenFileNotFound() {
        assertThrows(IOException.class, () -> new Adjective("/nonexistent/path/adjectives.txt"));
    }

    /**
     * Tests that the default constructor loads adjectives successfully.
     * This assumes the real dictionary file exists at the default location.
     */
    @Test
    public void testDefaultConstructorLoadsAdjectives() {
        // This assumes the real dictionary file exists at the default location
        assertDoesNotThrow(() -> new Adjective());
    }
}