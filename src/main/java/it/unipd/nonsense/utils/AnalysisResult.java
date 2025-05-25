package it.unipd.nonsense.utils;

import com.google.cloud.language.v1.AnalyzeSyntaxResponse;
import java.util.Map;
import java.util.Set;

/**
 * A container class that holds the results of linguistic analysis.
 * Stores the original text along with its syntactic analysis results
 * and words categorized by their grammatical types.
 */
public class AnalysisResult {
    /**
     * The original input text that was analyzed
     */
    private String originalText;

    /**
     * The syntactic analysis response from Google Cloud Natural Language API
     */
    private AnalyzeSyntaxResponse syntaxResponse;

    /**
     * A map categorizing words by their grammatical types (NOUN, VERB, ADJ etc.)
     * where the key is the grammatical type and the value is a set of words
     */
    private Map<String, Set<String>> wordsByType;

    /**
     * Gets the original text that was analyzed
     *
     * @return The original input text
     */
    public String getOriginalText() {
        return originalText;
    }

    /**
     * Sets the original text that was analyzed
     *
     * @param originalText The input text to store
     */
    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    /**
     * Gets the full syntactic analysis response from the API
     *
     * @return AnalyzeSyntaxResponse containing detailed linguistic analysis
     */
    public AnalyzeSyntaxResponse getSyntaxResponse() {
        return syntaxResponse;
    }

    /**
     * Sets the syntactic analysis response
     *
     * @param syntaxResponse The API response to store
     */
    public void setSyntaxResponse(AnalyzeSyntaxResponse syntaxResponse) {
        this.syntaxResponse = syntaxResponse;
    }

    /**
     * Gets the words categorized by their grammatical types
     *
     * @return Map where keys are grammatical types (e.g., "NOUN")
     *         and values are sets of words belonging to each type
     */
    public Map<String, Set<String>> getWordsByType() {
        return wordsByType;
    }

    /**
     * Sets the categorized words collection
     *
     * @param wordsByType Map of words organized by grammatical types
     */
    public void setWordsByType(Map<String, Set<String>> wordsByType) {
        this.wordsByType = wordsByType;
    }
}