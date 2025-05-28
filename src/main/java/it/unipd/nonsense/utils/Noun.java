package it.unipd.nonsense.utils;

import java.io.IOException;

/**
 * A specialized word dictionary implementation for nouns.
 * Extends the base {@link Words} class to provide noun-specific functionality.
 * Loads nouns from a predefined text file in the application resources.
 */
public class Noun extends Words {
    private static final String DEFAULT_NOUN_PATH = "/it/unipd/nonsense/dictionaries/nouns.txt";

    /**
     * Constructs a new noun dictionary using the default path.
     * @throws IOException if the nouns dictionary file cannot be read
     */
    public Noun() throws IOException {
        this(DEFAULT_NOUN_PATH);
    }

    /**
     * Constructs a new noun dictionary with a custom path (for testing)
     * @param dictionaryPath path to the nouns dictionary
     * @throws IOException if the dictionary file cannot be read
     */
    Noun(String dictionaryPath) throws IOException {
        super(dictionaryPath);
    }
}