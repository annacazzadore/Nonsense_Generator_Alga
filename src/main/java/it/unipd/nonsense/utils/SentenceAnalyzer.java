package it.unipd.nonsense.utils;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.language.v1.*;
import com.google.cloud.language.v1.Document.Type;
import com.google.api.gax.core.FixedCredentialsProvider;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * A service for analyzing sentence structure and content using Google Cloud Natural Language API.
 * Provides functionality for toxicity detection, syntactic analysis, and dictionary management.
 * Implements AutoCloseable to ensure proper resource cleanup.
 */
public class SentenceAnalyzer implements AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(SentenceAnalyzer.class.getName());

    // Path configuration constants
    private static final String DICTIONARIES_DIR = "src/resources/dictionaries/";
    private static final String CONFIG_FILE = "src/resources/config.properties";

    // Used language
    private static final String ENGLISH_LANGUAGE_CODE = "en";

    // Google Cloud Language service client
    private final LanguageServiceClient language;

    // Toxicity analysis constants
    private static final float DEFAULT_TOXICITY_SCORE = 0.0f;
    private static final Set<String> TOXIC_CATEGORIES = Set.of("Toxic", "Insult", "Profanity");

    // Cache configuration for toxicity scores
    private static final int MAX_CACHE_SIZE = 1000;
    private static final Map<String, Float> toxicityCache = Collections.synchronizedMap(new LinkedHashMap<>(MAX_CACHE_SIZE, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Float> eldest) {
            return size() > MAX_CACHE_SIZE;
        }
    });

    /**
     * Calculates a toxicity score for the given sentence using Google's Perspective API.
     * Implements caching to improve performance for repeated sentences.
     *
     * @param sentence The text to analyze for toxic content
     * @return A score between 0 (non-toxic) and 1 (highly toxic)
     */
    public float getToxicityScore(String sentence) {
        // Check if there is a real sentence
        if (sentence == null || sentence.trim().isEmpty()) {
            return DEFAULT_TOXICITY_SCORE;
        }

        // Check cache first
        Float cachedScore = toxicityCache.get(sentence);
        if (cachedScore != null) {
            return cachedScore;
        }

        // Calculate toxicity score
        float score = calculateToxicityScore(sentence);

        // Update cache
        synchronized (toxicityCache) {
            toxicityCache.put(sentence, score);
        }

        return score;
    }

    /**
     * Internal method to calculate toxicity score using Google's Natural Language API.
     *
     * @param sentence The text to analyze
     * @return The highest confidence score among toxic categories
     */
    private float calculateToxicityScore(String sentence) {
        try {
            // Create a document object for the API request
            Document doc = Document.newBuilder().setContent(sentence).setType(Type.PLAIN_TEXT).build();

            // Call the moderation API
            ModerateTextResponse response = language.moderateText(doc);

            // Find the most confident toxic category
            for (ClassificationCategory category : response.getModerationCategoriesList()) {
                if (TOXIC_CATEGORIES.contains(category.getName())) {
                    return category.getConfidence();
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error calculating toxicity score", e);
        }

        return DEFAULT_TOXICITY_SCORE;
    }

    /**
     * Constructor that initializes the SentenceAnalyzer with Google Cloud credentials.
     * Loads configuration and sets up the language service client.
     *
     * @throws IOException If configuration or credentials cannot be loaded
     */
    public SentenceAnalyzer() throws IOException {
        Properties props = loadConfiguration();
        String credPath = getCredentialsPath(props);
        GoogleCredentials credentials = loadCredentials(credPath);
        this.language = createLanguageClient(credentials);
    }

    /**
     * Loads application configuration from properties file.
     *
     * @return Loaded properties
     * @throws IOException If configuration file cannot be read
     */
    private Properties loadConfiguration() throws IOException {
        Properties props = new Properties();

        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            // Load properties from file
            props.load(fis);
            return props;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Configuration file not found or unreadable", e);
            throw new IOException("Configuration file " + CONFIG_FILE + " not found or unreadable", e);
        }
    }

    /**
     * Gets the credentials path based on operating system.
     *
     * @param props Application properties
     * @return Path to credentials file
     * @throws IOException If OS is not supported
     */
    private String getCredentialsPath(Properties props) throws IOException {
        // Detecting OS
        String os = System.getProperty("os.name").toLowerCase();
        // Select appropriate credential path based on OS
        String credPath = os.contains("win") ? props.getProperty("windows.key")
                : os.contains("mac") ? props.getProperty("macos.key")
                : null;

        // If there is no path, an error message is displayed
        if (credPath == null) {
            String errorMsg = "Key path not configured for OS: " + os;
            LOGGER.severe(errorMsg);
            throw new IOException(errorMsg);
        }

        return credPath;
    }

    /**
     * Loads Google Cloud credentials from JSON file.
     *
     * @param credPath Path to credentials file
     * @return GoogleCredentials object
     * @throws IOException If credentials cannot be loaded
     */
    private GoogleCredentials loadCredentials(String credPath) throws IOException {
        try (FileInputStream credStream = new FileInputStream(credPath)) {
            // Create credentials with default language service scopes
            return GoogleCredentials.fromStream(credStream).createScoped(LanguageServiceSettings.getDefaultServiceScopes());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unable to find or read credentials file", e);
            throw new IOException("Unable to find or read credentials file at: " + credPath, e);
        }
    }

    /**
     * Creates a LanguageServiceClient with the given credentials.
     *
     * @param credentials Google Cloud credentials
     * @return Configured LanguageServiceClient
     * @throws IOException If client cannot be created
     */
    private LanguageServiceClient createLanguageClient(GoogleCredentials credentials) throws IOException {
        // Build settings with the provided credentials
        LanguageServiceSettings settings = LanguageServiceSettings.newBuilder().setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build();

        // Create and return the client
        return LanguageServiceClient.create(settings);
    }

    /**
     * Performs syntactic analysis of a sentence.
     *
     * @param sentence The text to analyze
     * @return Analysis response containing tokens and dependencies
     */
    public AnalyzeSyntaxResponse analyzeSentence(String sentence) {
        // Create document object for API request
        Document doc = createDocument(sentence);
        // Perform syntactic analysis

        return language.analyzeSyntax(doc);
    }

    /**
     * Extracts words from analysis response grouped by part of speech.
     *
     * @param response The syntactic analysis response
     * @return Map of word types to word sets
     */
    public Map<String, Set<String>> extractWordsByType(AnalyzeSyntaxResponse response) {
        // Initialize empty dictionary structure
        Map<String, Set<String>> dictionary = initializeDictionary();

        // Process each token in the response
        response.getTokensList().forEach(token -> {
            String word = token.getText().getContent().toLowerCase();
            // Get part of speech tag
            String pos = token.getPartOfSpeech().getTag().name();

            // Add word to appropriate dictionary based on pos
            switch (pos) {
                case "NOUN" -> dictionary.get("NOUN").add(word);
                case "VERB" -> dictionary.get("VERB").add(word);
                case "ADJ" -> dictionary.get("ADJ").add(word);
            }
        });

        return dictionary;
    }

    /**
     * Initializes an empty dictionary structure.
     */
    private Map<String, Set<String>> initializeDictionary() {
        return Map.of(
                "NOUN", new HashSet<>(),
                "VERB", new HashSet<>(),
                "ADJ", new HashSet<>()
        );
    }

    /**
     * Saves extracted words to their respective dictionary files.
     *
     * @param words Map of words by type
     * @throws IOException If dictionary files cannot be written
     */
    public void saveWordsToDictionaries(Map<String, Set<String>> words) throws IOException {
        saveDictionary("nouns.txt", words.get("NOUN"));
        saveDictionary("verbs.txt", words.get("VERB"));
        saveDictionary("adjectives.txt", words.get("ADJ"));
    }

    /**
     * Saves new words to a dictionary file.
     *
     * @param fileName Target dictionary file name
     * @param newWords Set of words to save
     * @throws IOException If file operations fail
     */
    private void saveDictionary(String fileName, Set<String> newWords) throws IOException {
        // Skip if no words to save
        if (newWords == null || newWords.isEmpty()) {
            return;
        }

        // Create file object for dictionary
        File file = new File(DICTIONARIES_DIR + fileName);
        ensureFileExists(file);

        // Load existing words and filter out duplicates
        Set<String> existingWords = loadDictionary(file);
        List<String> wordsToAppend = filterNewWords(newWords, existingWords);

        // Append new words to file if any
        if (!wordsToAppend.isEmpty()) {
            appendToFile(file, wordsToAppend);
        }
    }

    /**
     * Ensures target file exists, creating parent directories if needed.
     */
    private void ensureFileExists(File file) throws IOException {
        if (!file.exists()) {
            // Create parent directories if they don't exist
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
    }

    /**
     * Filters out words that already exist in the dictionary.
     */
    private List<String> filterNewWords(Set<String> newWords, Set<String> existingWords) {
        // Keep only words not already in dictionary
        return newWords.stream().filter(word -> !existingWords.contains(word)).collect(Collectors.toList());
    }

    /**
     * Appends words to dictionary file.
     */
    private void appendToFile(File file, List<String> words) throws IOException {
        try (FileWriter fw = new FileWriter(file, true)) {
            // true = append mode
            // Write each word on a new line
            for (String word : words) {
                fw.write(word + System.lineSeparator());
            }
        }
    }

    /**
     * Loads existing words from dictionary file.
     */
    private Set<String> loadDictionary(File file) throws IOException {
        // Return empty set if file doesn't exist
        if (!file.exists()) {
            return new HashSet<>();
        }

        try (Scanner scanner = new Scanner(file)) {
            Set<String> words = new HashSet<>();
            // Read each line and add to set
            while (scanner.hasNextLine()) {
                words.add(scanner.nextLine().trim().toLowerCase());
            }
            return words;
        }
    }

    /**
     * Detects if text is in English language.
     *
     * @param text The text to analyze
     * @return true if text is identified as English
     */
    public boolean isEnglish(String text) {
        // Create document for API request
        Document doc = createDocument(text);
        AnalyzeEntitiesResponse response = language.analyzeEntities(
                AnalyzeEntitiesRequest.newBuilder()
                        .setDocument(doc)
                        .setEncodingType(EncodingType.UTF8)
                        .build());
        // Check if detected language is English
        return ENGLISH_LANGUAGE_CODE.equals(response.getLanguage());
    }

    /**
     * Creates a document object for API requests.
     */
    private Document createDocument(String content) {
        return Document.newBuilder()
                .setContent(content)
                .setType(Type.PLAIN_TEXT)
                .build();
    }

    /**
     * Closes the LanguageServiceClient and releases resources.
     */
    @Override
    public void close() {
        if (language != null) {
            try {
                language.close();
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Error closing LanguageServiceClient", e);
            }
        }
    }
}