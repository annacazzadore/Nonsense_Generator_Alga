package it.unipd.nonsense.utils;

import java.io.IOException;

/**
 * A specialized word dictionary implementation for adjectives.
 * Extends the base {@link Words} class to provide adjective-specific functionality.
 * Loads adjectives from a predefined text file in the application resources.
 */
public class Adjective extends Words {
    private static final String DEFAULT_ADJ_PATH = "/it/unipd/nonsense/dictionaries/adjectives.txt";

    /**
     * Constructs a new adjective dictionary using the default path.
     * @throws IOException if the adjectives dictionary file cannot be read
     */
    public Adjective() throws IOException {
        this(DEFAULT_ADJ_PATH);
    }

    /**
     * Constructs a new adjective dictionary with a custom path (for testing)
     * @param dictionaryPath path to the adjectives dictionary
     * @throws IOException if the dictionary file cannot be read
     */
    Adjective(String dictionaryPath) throws IOException {
        super(dictionaryPath);
    }
}