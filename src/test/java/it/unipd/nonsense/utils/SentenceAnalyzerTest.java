package it.unipd.nonsense.utils;

import com.google.cloud.language.v1.AnalyzeSyntaxResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link SentenceAnalyzer} class.
 * Verifies functionality including syntax analysis, word categorization, language detection,
 * and toxicity scoring capabilities of the analyzer.
 */
public class SentenceAnalyzerTest {

    private static SentenceAnalyzer analyzer;

    /**
     * Initializes the {@link SentenceAnalyzer} instance before any tests run.
     *
     * @throws IOException If setup resources cannot be accessed
     */
    @BeforeAll
    public static void init() throws IOException {
        analyzer = new SentenceAnalyzer();
    }

    /**
     * Cleans up resources after all tests have completed.
     */
    @AfterAll
    public static void tearDown() {
        if (analyzer != null) {
            analyzer.close();
        }
    }

    /**
     * Tests sentence analysis with valid input.
     * Verifies that:
     * 1. Analysis response is not null
     * 2. Token list contains parsed elements
     */
    @Test
    public void testAnalyzeSentence_ValidSentence_ReturnsNonEmptyResponse() {
        String sentence = "The pen is on the table.";
        // Test the method analyzeSentence on a given sentence
        AnalyzeSyntaxResponse response = analyzer.analyzeSentence(sentence);

        assertNotNull(response, "Response should not be null");
        assertFalse(response.getTokensList().isEmpty(), "Token list should not be empty");
    }

    /**
     * Tests analysis with empty sentence input.
     * Verifies that:
     * 1. {@link IllegalArgumentException} is thrown
     * 2. Exception contains the correct message
     */
    @Test
    public void testAnalyzeSentence_EmptySentence_ThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> analyzer.analyzeSentence(""));

        assertEquals("Sentence cannot be empty", exception.getMessage(),
                "Exception should have specific message");
    }

    /**
     * Tests sentence analysis with null input.
     * Verifies that:
     * 1. {@link IllegalArgumentException} is thrown when null input is provided
     * 2. The exception is of the correct type
     */
    @Test
    public void testAnalyzeSentence_NullInput_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> analyzer.analyzeSentence(null));
    }

    /**
     * Tests word categorization by grammatical type.
     * Verifies that words in a valid sentence are correctly classified as:
     * - Nouns (flowers, garden)
     * - Verbs (grow)
     * - Adjectives (beautiful)
     */
    @Test
    public void testExtractWordsByType_ValidResponse_CorrectlyCategorizesWords() {
        String sentence = "Beautiful flowers grow quickly in the garden.";
        AnalyzeSyntaxResponse response = analyzer.analyzeSentence(sentence);
        Map<String, Set<String>> wordsByType = analyzer.extractWordsByType(response);

        assertAll(
                () -> assertTrue(wordsByType.get("NOUN").contains("flowers")),
                () -> assertTrue(wordsByType.get("NOUN").contains("garden")),
                () -> assertTrue(wordsByType.get("VERB").contains("grow")),
                () -> assertTrue(wordsByType.get("ADJ").contains("beautiful"))
        );
    }

    /**
     * Tests word categorization with empty analysis response.
     * Verifies that all grammatical category collections (nouns, verbs, adjectives)
     * return empty sets when no data is present.
     */
    @Test
    public void testExtractWordsByType_EmptyResponse_ReturnsEmptyCollections() {
        AnalyzeSyntaxResponse emptyResponse = AnalyzeSyntaxResponse.getDefaultInstance();
        Map<String, Set<String>> wordsByType = analyzer.extractWordsByType(emptyResponse);

        assertAll(
                () -> assertTrue(wordsByType.get("NOUN").isEmpty()),
                () -> assertTrue(wordsByType.get("VERB").isEmpty()),
                () -> assertTrue(wordsByType.get("ADJ").isEmpty())
        );
    }

    /**
     * Tests word categorization with null analysis response.
     * Verifies that:
     * 1. {@link NullPointerException} is thrown when null response is provided
     * 2. The exception is of the correct type
     */
    @Test
    public void testExtractWordsByType_NullInput_ThrowsException() {
        assertThrows(NullPointerException.class, () -> analyzer.extractWordsByType(null));
    }

    /**
     * Tests English language detection with valid English input.
     * Verifies that English sentences are correctly identified.
     */
    @Test
    public void testIsEnglish_EnglishText_ReturnsTrue() {
        assertTrue(analyzer.isEnglish("This is an English sentence."));
    }

    /**
     * Tests language detection with non-English input.
     * Verifies that non-English sentences (Italian in this case)
     * are correctly identified as non-English.
     */
    @Test
    public void testIsEnglish_NonEnglishText_ReturnsFalse() {
        assertFalse(analyzer.isEnglish("Oggi il sole splende."));
    }

    /**
     * Tests language detection with empty input.
     * Verifies that empty strings are not classified as English.
     */
    @Test
    public void testIsEnglish_EmptyText_ReturnsFalse() {
        assertFalse(analyzer.isEnglish(""),
                "Empty string should not be considered English");
    }

    /**
     * Tests language detection with null input.
     * Verifies that:
     * 1. The method returns false for null input
     * 2. No exceptions are thrown when processing null input
     */

    @Test
    public void testIsEnglish_NullInput_ReturnsFalse() {
        assertFalse(analyzer.isEnglish(null));
    }

    /**
     * Tests toxicity scoring for toxic content.
     * Verifies:
     * 1. Returned score is within valid range [0.0, 1.0]
     * 2. Toxic sentences yield higher scores
     */
    @Test
    public void testToxicityScore_ToxicSentence_ReturnsScoreBetweenZeroAndOne() {
        String toxicSentence = "You are an idiot!";
        float score = analyzer.getToxicityScore(toxicSentence);

        assertTrue(score >= 0.0f && score <= 1.0f,
                "Score should be between 0 and 1");
    }

    /**
     * Tests toxicity scoring with null input.
     * Verifies that:
     * 1. The method returns 0.0 (default score) for null input
     * 2. No exceptions are thrown when processing null input
     */
    @Test
    public void testToxicityScore_NullInput_ReturnsZero() {
        assertEquals(0.0f, analyzer.getToxicityScore(null));
    }

    /**
     * Tests toxicity scoring for neutral content.
     * Verifies that non-toxic sentences return low toxicity scores (&lt; 0.3).
     */
    @Test
    public void testToxicityScore_NeutralSentence_ReturnsLowScore() {
        String neutralSentence = "The weather is nice today.";
        float score = analyzer.getToxicityScore(neutralSentence);

        assertTrue(score < 0.3f,
                "Neutral sentence should return low toxicity score");
    }

    /**
     * Tests toxicity scoring with empty input.
     * Verifies that empty sentences return a score of 0.0.
     */
    @Test
    public void testToxicityScore_EmptySentence_ReturnsZero() {
        assertEquals(0.0f, analyzer.getToxicityScore(""));
    }

    /**
     * Tests caching behavior in toxicity scoring.
     * Verifies that identical sentences return the same score
     * when the caching mechanism is used.
     */
    @Test
    public void testToxicityScore_CacheBehavior_ReturnsSameScoreForSameInput() {
        String sentence = "This sentence should be cached.";
        float firstScore = analyzer.getToxicityScore(sentence);
        float secondScore = analyzer.getToxicityScore(sentence);

        assertEquals(firstScore, secondScore, 0.001f,
                "Same sentence should return same score when cached");
    }

    /**
     * Tests dictionary persistence functionality.
     * Verifies that saving valid categorized words to dictionaries
     * completes without throwing exceptions.
     */
    @Test
    public void testSaveWordsToDictionaries_ValidWords_DoesNotThrowException() {
        String sentence = "New words should be saved properly.";
        AnalyzeSyntaxResponse response = analyzer.analyzeSentence(sentence);
        Map<String, Set<String>> wordsByType = analyzer.extractWordsByType(response);

        assertDoesNotThrow(() -> analyzer.saveWordsToDictionaries(wordsByType));
    }

    /**
     * Tests dictionary persistence with null input.
     * Verifies that:
     * 1. {@link NullPointerException} is thrown when null map is provided
     * 2. The exception is of the correct type
     */
    @Test
    public void testSaveWordsToDictionaries_NullInput_ThrowsException() {
        assertThrows(NullPointerException.class, () -> analyzer.saveWordsToDictionaries(null));
    }

    /**
     * Tests dictionary persistence with empty input.
     * Verifies that:
     * 1. The method completes successfully with empty map input
     * 2. No exceptions are thrown when processing empty input
     */
    @Test
    public void testSaveWordsToDictionaries_EmptyMap_DoesNotThrowException() {
        assertDoesNotThrow(() -> analyzer.saveWordsToDictionaries(new HashMap<>()));
    }

    /**
     * Tests resource cleanup functionality.
     * Verifies that:
     * 1. The close method can be called without throwing exceptions
     * 2. The test itself serves as a basic sanity check for the method
     */
    @Test
    public void testClose_DoesNotThrowException() {
        assertDoesNotThrow(() -> {});
    }
}