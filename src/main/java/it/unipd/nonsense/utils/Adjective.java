package it.unipd.nonsense.utils;

import java.io.IOException;

/**
 * A specialized word dictionary implementation for adjectives.
 * Extends the base {@link Words} class to provide adjectives-specific functionality.
 * Loads adjectives from a predefined text file in the application resources.
 */
public class Adjective extends Words {

    /**
     * Constructs a new adjective dictionary by loading adjectives from the default adjectives file.
     * The adjectives are loaded from "dictionaries/adjectives.txt" in the application resources.
     *
     * @throws IOException if the adjectives dictionary file cannot be found at the specified path,
     *                    or if there's an error reading the file
     */
    public Adjective() throws IOException {
        super("dictionaries/adjectives.txt");
    }
}