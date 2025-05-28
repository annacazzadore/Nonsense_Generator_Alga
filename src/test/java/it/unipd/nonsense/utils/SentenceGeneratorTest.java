package it.unipd.nonsense.utils;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SentenceGeneratorTest {

    @Test
    void testGenerateSentencesReturnsExpectedOutput() throws IOException {
        // Arrange
        SentenceGenerator generator = new SentenceGenerator();
        int count = 2;
        String template = "The [adjective] [noun] [verb] in the [adjective] [noun].";

        List<String> nouns = List.of("dog", "garden", "robot");
        List<String> verbs = List.of("runs", "flies", "sleeps");
        List<String> adjectives = List.of("happy", "blue", "ancient");

        // Act
        List<String> sentences = generator.generateSentences(count, template, nouns, verbs, adjectives);

        // Assert
        assertNotNull(sentences);
        assertEquals(count, sentences.size());
        for (String sentence : sentences) {
            assertFalse(sentence.contains("[noun]"));
            assertFalse(sentence.contains("[verb]"));
            assertFalse(sentence.contains("[adjective]"));
            assertTrue(sentence.matches("(?i).*\\w+.*")); // Contains at least one word
        }
    }
}