package it.unipd.nonsense.utils;

import java.io.IOException;

/**
 * A specialized word dictionary implementation for verbs.
 * Extends the base {@link Words} class to provide verb-specific functionality.
 * Loads verbs from a predefined text file in the application resources.
 */
public class Verb extends Words {
    private static final String DEFAULT_VERB_PATH = "/dictionaries/verbs.txt";

    /**
     * Constructs a new verb dictionary using the default path.
     * The verbs are loaded from "dictionaries/verbs.txt" in the application resources.
     * @throws IOException if the verbs dictionary file cannot be found at the specified path,
     *                    or if there's an error reading the file
     */
    public Verb() throws IOException {
        this(DEFAULT_VERB_PATH);
    }

    /**
     * Constructs a new verb dictionary with a custom path (for testing)
     * @param dictionaryPath path to the verbs dictionary
     * @throws IOException if the dictionary file cannot be read
     */
    Verb(String dictionaryPath) throws IOException {
        super(dictionaryPath);
    }
}