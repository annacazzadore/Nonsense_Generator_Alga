package it.unipd.nonsense.utils;

import java.io.IOException;
import java.util.*;

/**
 * A sentence generator that creates sentences based on templates with placeholders.
 * Replaces placeholders ([noun], [verb], [adjective]) with random words from dictionaries.
 */
public class SentenceGenerator {

    /**
     * Constructs a new SentenceGenerator.
     * @throws IOException if dictionary files cannot be loaded
     */
    public SentenceGenerator() throws IOException {}

    /**
     * Generates multiple sentences based on a template with placeholders.
     *
     * @param count Number of sentences to generate (must be > 0)
     * @param template Sentence template with placeholders ([noun], [verb], [adjective])
     * @param nouns Optional list of nouns to use (uses dictionary if null/empty)
     * @param verbs Optional list of verbs to use (uses dictionary if null/empty)
     * @param adjectives Optional list of adjectives to use (uses dictionary if null/empty)
     * @return List of generated sentences
     * @throws IOException if dictionary files cannot be accessed
     * @throws IllegalArgumentException for invalid parameters
     */
    public List<String> generateSentences(int count, String template, List<String> nouns, List<String> verbs, List<String> adjectives) throws IOException {
        long start = System.nanoTime();

        // Validate input parameters
        if (count <= 0) {
            throw new IllegalArgumentException("The number of sentences must be greater than 0");
        }
        if (template == null || template.isEmpty() || template.equals("-")) {
            throw new IllegalArgumentException("Select a valid template");
        }

        List<String> sentences = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < count; i++) {
            String sentence = template;

            // Use provided words or fall back to dictionary
            List<String> currentNouns = (nouns == null || nouns.isEmpty())
                    ? getAllNounsFromDictionary() : new ArrayList<>(nouns);
            List<String> currentVerbs = (verbs == null || verbs.isEmpty())
                    ? getAllVerbsFromDictionary() : new ArrayList<>(verbs);
            List<String> currentAdjectives = (adjectives == null || adjectives.isEmpty())
                    ? getAllAdjectivesFromDictionary() : new ArrayList<>(adjectives);

            // Shuffle words for randomness
            Collections.shuffle(currentNouns, rand);
            Collections.shuffle(currentVerbs, rand);
            Collections.shuffle(currentAdjectives, rand);

            // Replace placeholders with words
            sentence = replacePlaceholders(sentence, currentNouns, currentVerbs, currentAdjectives, rand);
            sentences.add(sentence);
        }

        // Log generation time
        long end = System.nanoTime();
        System.out.println("Time generation phrases (" + count + "): " + (end - start) / 1_000_000 + " ms");

        return sentences;
    }

    /**
     * Replaces all placeholders in a template with random words.
     *
     * @param template Sentence template with placeholders
     * @param nouns List of available nouns
     * @param verbs List of available verbs
     * @param adjectives List of available adjectives
     * @param rand Random number generator
     * @return Sentence with placeholders replaced
     */
    private String replacePlaceholders(String template, List<String> nouns, List<String> verbs, List<String> adjectives, Random rand) {
        String result = template;

        // Count each type of placeholder
        int nounCount = countPlaceholders(result, "\\[noun]");
        int verbCount = countPlaceholders(result, "\\[verb]");
        int adjCount = countPlaceholders(result, "\\[adjective]");

        try {
            // Ensure we have enough words of each type
            if (nounCount > 0) {
                Noun nounGenerator = new Noun();
                while (nouns.size() < nounCount) {
                    nouns.addAll(nounGenerator.getRandomFromDictionary(nounCount - nouns.size()));
                }
            }

            if (verbCount > 0) {
                Verb verbGenerator = new Verb();
                while (verbs.size() < verbCount) {
                    verbs.addAll(verbGenerator.getRandomFromDictionary(verbCount - verbs.size()));
                }
            }

            if (adjCount > 0) {
                Adjective adjectiveGenerator = new Adjective();
                while (adjectives.size() < adjCount) {
                    adjectives.addAll(adjectiveGenerator.getRandomFromDictionary(adjCount - adjectives.size()));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load word dictionaries", e);
        }

        // Create pools of words for replacement
        List<String> nounPool = new ArrayList<>(nouns.subList(0, nounCount));
        List<String> verbPool = new ArrayList<>(verbs.subList(0, verbCount));
        List<String> adjPool = new ArrayList<>(adjectives.subList(0, adjCount));

        // Shuffle the pools
        Collections.shuffle(nounPool, rand);
        Collections.shuffle(verbPool, rand);
        Collections.shuffle(adjPool, rand);

        // Perform the replacements
        for (String noun : nounPool) {
            result = result.replaceFirst("\\[noun]", noun);
        }
        for (String verb : verbPool) {
            result = result.replaceFirst("\\[verb]", verb);
        }
        for (String adj : adjPool) {
            result = result.replaceFirst("\\[adjective]", adj);
        }

        return result;
    }

    /**
     * Counts occurrences of a placeholder in a string.
     *
     * @param text The string to search
     * @param placeholderRegex The placeholder pattern to count
     * @return Number of placeholder occurrences
     */
    private int countPlaceholders(String text, String placeholderRegex) {
        return text.split(placeholderRegex, -1).length - 1;
    }

    /**
     * Gets random nouns from the dictionary.
     *
     * @return List of 10 random nouns
     * @throws IOException if noun dictionary cannot be accessed
     */
    private List<String> getAllNounsFromDictionary() throws IOException {
        Noun nounGenerator = new Noun();
        List<String> nouns = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            nouns.add(nounGenerator.getRandom());
        }
        return nouns;
    }

    /**
     * Gets random verbs from the dictionary.
     *
     * @return List of 10 random verbs
     * @throws IOException if verb dictionary cannot be accessed
     */
    private List<String> getAllVerbsFromDictionary() throws IOException {
        Verb verbGenerator = new Verb();
        List<String> verbs = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            verbs.add(verbGenerator.getRandom());
        }
        return verbs;
    }

    /**
     * Gets random adjectives from the dictionary.
     *
     * @return List of 10 random adjectives
     * @throws IOException if adjective dictionary cannot be accessed
     */
    private List<String> getAllAdjectivesFromDictionary() throws IOException {
        Adjective adjectiveGenerator = new Adjective();
        List<String> adjectives = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            adjectives.add(adjectiveGenerator.getRandom());
        }
        return adjectives;
    }
}