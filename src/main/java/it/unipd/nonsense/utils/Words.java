package it.unipd.nonsense.utils;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * An abstract base class for word dictionary operations.
 * Provides functionality to load words from a dictionary file and retrieve random words.
 */
public abstract class Words {
    // List to store words loaded from the dictionary
    protected List<String> words;
    // Random number generator for selecting random words
    private final Random rand = new Random();

    /**
     * Constructs a Words instance by loading words from the specified dictionary file.
     *
     * @param dictionaryPath the path to the dictionary file within the resources folder
     * @throws IOException if the dictionary file cannot be found or read
     */
    public Words(String dictionaryPath) throws IOException {
        // Get the dictionary file as an input stream from resources
        InputStream input = getClass().getResourceAsStream(dictionaryPath);

        if (input == null) {
            throw new IOException("Dictionary file not found: " + dictionaryPath);
        }

        // Try-with-resources to ensure the reader is closed automatically
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            // Read all lines from the file and collect them into a List
            words = reader.lines().collect(Collectors.toList());
        }
    }

    /**
     * Gets a random word from the loaded dictionary.
     *
     * @return a randomly selected word from the dictionary
     */
    public String getRandom() {
        return words.get(rand.nextInt(words.size()));
    }

    /**
     * Gets a list of random words from the dictionary.
     *
     * @param count the number of random words to retrieve
     * @return a List containing the requested number of random words
     * @throws IOException if there's an error accessing the dictionary
     */
    protected List<String> getRandomFromDictionary(int count) throws IOException {
        List<String> result = new ArrayList<>();

        // Add the specified number of random words to the result list
        for (int i = 0; i < count; i++) {
            result.add(getRandom());
        }
        return result;
    }
}