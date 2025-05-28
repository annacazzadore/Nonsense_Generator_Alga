package it.unipd.nonsense.utils;

import com.google.cloud.language.v1.AnalyzeSyntaxResponse;
import it.unipd.nonsense.utils.SentenceAnalyzer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class SentenceAnalyzerTest {
    private SentenceAnalyzer analyzer;

    @BeforeEach
    void setUp()  throws IOException{
        analyzer = new SentenceAnalyzer();
    }

    @Test
    void testAnalyzeSentenceReturnsValidResponse() throws IOException {
        AnalyzeSyntaxResponse response = analyzer.analyzeSentence("Il gatto salta sul tavolo");
        assertNotNull(response);
        assertFalse(response.getTokensList().isEmpty());
    }




}