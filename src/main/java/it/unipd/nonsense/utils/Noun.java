package it.unipd.nonsense.utils;

import java.io.IOException;

/**
 * A specialized word dictionary implementation for nouns.
 * Extends the base {@link Words} class to provide noun-specific functionality.
 * Loads nouns from a predefined text file in the application resources.
 */
public class Noun extends Words
{

    /**
     * Constructs a new noun dictionary by loading nouns from the default nouns file.
     * The nouns are loaded from "dictionaries/nouns.txt" in the application resources.
     *
     * @throws IOException if the nouns dictionary file cannot be found at the specified path,
     *                    or if there's an error reading the file
     */
    public Noun() throws IOException
    {
        super("dictionaries/nouns.txt");
    }
}