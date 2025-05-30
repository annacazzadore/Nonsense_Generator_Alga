package it.unipd.nonsense.utils;

import com.google.cloud.language.v1.AnalyzeSyntaxResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link AnalysisResult}.
 * This class contains unit tests to verify the functionality of the AnalysisResult class,
 * including its getter and setter methods for various properties.
 *
 * <p>The tests cover:
 * <ul>
 *   <li>Basic getter/setter functionality</li>
 *   <li>Edge cases (empty strings, null values)</li>
 *   <li>Map operations and immutability</li>
 *   <li>Combined state verification</li>
 * </ul>
 */
public class AnalysisResultTest {

    private AnalysisResult analysisResult;

    /**
     * Sets up a fresh instance of AnalysisResult before each test method is executed.
     * This ensures test isolation and prevents side effects between tests.
     */
    @BeforeEach
    public void setUp() {
        analysisResult = new AnalysisResult();
    }

    /**
     * Tests the setOriginalText and getOriginalText methods with a normal string.
     * Verifies that the text is correctly stored and retrieved.
     */
    @Test
    public void testSetAndGetOriginalText() {
        String text = "This is a test.";
        analysisResult.setOriginalText(text);
        assertEquals(text, analysisResult.getOriginalText());
    }

    /**
     * Tests the setOriginalText and getOriginalText methods with an empty string.
     * Verifies that empty strings are handled correctly.
     */
    @Test
    public void testSetAndGetOriginalTextWithEmptyString() {
        analysisResult.setOriginalText("");
        assertEquals("", analysisResult.getOriginalText());
    }

    /**
     * Tests the setSyntaxResponse and getSyntaxResponse methods.
     * Verifies that an AnalyzeSyntaxResponse object can be properly stored and retrieved.
     */
    @Test
    public void testSetAndGetSyntaxResponse() {
        AnalyzeSyntaxResponse response = AnalyzeSyntaxResponse.newBuilder().build();
        analysisResult.setSyntaxResponse(response);
        assertEquals(response, analysisResult.getSyntaxResponse());
    }

    /**
     * Tests the setWordsByType and getWordsByType methods with a populated map.
     * Verifies that:
     * <ul>
     *   <li>The map is stored correctly</li>
     *   <li>All keys are preserved</li>
     *   <li>Values are correctly maintained in the sets</li>
     * </ul>
     */
    @Test
    public void testSetAndGetWordsByType() {
        Map<String, Set<String>> wordsMap = new HashMap<>();
        wordsMap.put("NOUN", new HashSet<>(Arrays.asList("dog", "cat")));
        wordsMap.put("VERB", new HashSet<>(Collections.singletonList("run")));

        analysisResult.setWordsByType(wordsMap);

        Map<String, Set<String>> result = analysisResult.getWordsByType();
        assertEquals(2, result.size());
        assertTrue(result.containsKey("NOUN"));
        assertTrue(result.get("NOUN").contains("dog"));
        assertTrue(result.get("VERB").contains("run"));
    }

    /**
     * Tests the setWordsByType and getWordsByType methods with an empty map.
     * Verifies that empty maps are handled correctly.
     */
    @Test
    public void testWordsByTypeWithEmptyMap() {
        analysisResult.setWordsByType(new HashMap<>());
        assertTrue(analysisResult.getWordsByType().isEmpty());
    }

    /**
     * Tests the setWordsByType and getWordsByType methods with a map containing null values.
     * Verifies that null values in the map are handled correctly.
     */
    @Test
    public void testWordsByTypeWithNullValue() {
        Map<String, Set<String>> wordsMap = new HashMap<>();
        wordsMap.put("ADJ", null);
        analysisResult.setWordsByType(wordsMap);

        assertTrue(analysisResult.getWordsByType().containsKey("ADJ"));
        assertNull(analysisResult.getWordsByType().get("ADJ"));
    }

    /**
     * Tests the immutability of the wordsByType map after being set in AnalysisResult.
     * Verifies that modifications to the returned map don't affect the internal state.
     */
    @Test
    public void testWordsByTypeImmutability() {
        Map<String, Set<String>> wordsMap = new HashMap<>();
        wordsMap.put("NOUN", new HashSet<>(Arrays.asList("dog", "cat")));
        analysisResult.setWordsByType(wordsMap);

        Map<String, Set<String>> result = analysisResult.getWordsByType();
        result.put("VERB", new HashSet<>(Collections.singletonList("jump")));
        assertFalse(analysisResult.getWordsByType().containsKey("VERB"));
    }

    /**
     * Tests that the syntaxResponse field can be set to null.
     * Verifies that null values are accepted for the syntax response.
     */
    @Test
    void testSetNullSyntaxResponse() {
        AnalysisResult result = new AnalysisResult();
        result.setSyntaxResponse(null);

        assertNull(result.getSyntaxResponse(), "Syntax response should be nullable");
    }

    /**
     * Tests the combined state of the AnalysisResult object when multiple properties are set.
     * Verifies that all properties maintain their values when set together.
     */
    @Test
    public void testCombinedState() {
        String text = "Sample text";
        AnalyzeSyntaxResponse response = AnalyzeSyntaxResponse.newBuilder().build();
        Map<String, Set<String>> wordsMap = new HashMap<>();
        wordsMap.put("NOUN", new HashSet<>(Arrays.asList("house", "car")));

        analysisResult.setOriginalText(text);
        analysisResult.setSyntaxResponse(response);
        analysisResult.setWordsByType(wordsMap);

        assertEquals(text, analysisResult.getOriginalText());
        assertEquals(response, analysisResult.getSyntaxResponse());
        assertEquals(wordsMap, analysisResult.getWordsByType());
    }
}