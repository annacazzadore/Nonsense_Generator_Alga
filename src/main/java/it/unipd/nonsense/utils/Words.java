package it.unipd.nonsense.utils;

import java.io.*;
import java.nio.file.*;
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
        List<String> loadedWords;

        Path filePath = Paths.get(dictionaryPath);
        if (Files.exists(filePath)) {
            // If it's a valid file path
            loadedWords = Files.readAllLines(filePath);
        } else {
            // Try to load it as a resource from classpath
            InputStream input = getClass().getResourceAsStream(dictionaryPath);
            if (input == null) {
                throw new IOException("Dictionary file not found: " + dictionaryPath);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
                loadedWords = reader.lines().collect(Collectors.toList());
            }
        }

        this.words = loadedWords;
    }


    /**
     * Gets a random word from the loaded dictionary.
     *
     * @return a randomly selected word from the dictionary
     */
    public String getRandom() {
        if (words.isEmpty()) {
            throw new NoSuchElementException("Cannot get random word from an empty dictionary.");
        }
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