package it.unipd.nonsense.utils;

import com.google.cloud.language.v1.AnalyzeSyntaxResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

public class SentenceAnalyzerTest {

    @Test
    void testAnalyzeSentenceReturnsValidResponse() throws IOException {
        try (SentenceAnalyzer analyzer = new SentenceAnalyzer()) {
            String sentence = "The cat jumps over the lazy dog.";
            AnalyzeSyntaxResponse response = analyzer.analyzeSentence(sentence);

            assertNotNull(response);
            assertFalse(response.getTokensList().isEmpty(), "Tokens should not be empty");
        }
    }
}
