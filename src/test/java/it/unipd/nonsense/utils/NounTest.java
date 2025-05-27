package it.unipd.nonsense.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NounTest {

    @Test
    void testGetRandomReturnsWord() throws Exception {
        Noun noun = new Noun();
        String word = noun.getRandom();
        assertNotNull(word);
        assertFalse(word.isBlank());
    }
}