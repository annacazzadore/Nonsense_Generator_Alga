package it.unipd.nonsense.utils;

import com.google.cloud.language.v1.AnalyzeSyntaxResponse;
import java.util.Map;
import java.util.Set;

public class AnalysisResult
{
    private String originalText;
    private AnalyzeSyntaxResponse syntaxResponse;
    private Map<String, Set<String>> wordsByType;

    public String getOriginalText() { return originalText; }
    public void setOriginalText(String originalText) { this.originalText = originalText; }

    public AnalyzeSyntaxResponse getSyntaxResponse() { return syntaxResponse; }
    public void setSyntaxResponse(AnalyzeSyntaxResponse syntaxResponse) {
        this.syntaxResponse = syntaxResponse;
    }

    public Map<String, Set<String>> getWordsByType() { return wordsByType; }
    public void setWordsByType(Map<String, Set<String>> wordsByType) {
        this.wordsByType = wordsByType;
    }
}