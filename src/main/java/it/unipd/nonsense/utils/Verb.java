package it.unipd.nonsense.utils;

import java.io.IOException;

/**
 * A specialized word dictionary implementation for verbs.
 * Extends the base {@link Words} class to provide verb-specific functionality.
 * Loads verbs from a predefined text file in the application resources.
 */
public class Verb extends Words {

    /**
     * Constructs a new verb dictionary by loading verbs from the default verbs file.
     * The verbs are loaded from "dictionaries/verbs.txt" in the application resources.
     *
     * @throws IOException if the verbs dictionary file cannot be found at the specified path,
     *                    or if there's an error reading the file
     */
    public Verb() throws IOException {
        super("dictionaries/verbs.txt");
    }
}