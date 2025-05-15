package it.unipd.nonsense.generator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;

class SentenceGeneratorTest {
    private SentenceGenerator generator;

    @BeforeEach
    void setUp() throws IOException {
        DictionaryManager dictionaryManager = new DictionaryManager();
        this.generator = new SentenceGenerator(dictionaryManager);
    }

    @Test
    void testGenerateSentence() {
        String sentence = generator.generateSentence();
        assertNotNull(sentence);
        assertFalse(sentence.isEmpty());
    }
}