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

public class SentenceAnalyzer implements AutoCloseable
{
    private static final Logger LOGGER = Logger.getLogger(SentenceAnalyzer.class.getName());

    private static final String DICTIONARIES_DIR = "src/resources/dictionaries/";
    private static final String CONFIG_FILE = "src/resources/config.properties";
    private static final String ENGLISH_LANGUAGE_CODE = "en";

    private final LanguageServiceClient language;
    private static final float DEFAULT_TOXICITY_SCORE = 0.0f;
    private static final Set<String> TOXIC_CATEGORIES = Set.of("Toxic", "Insult", "Profanity");
    private static final int MAX_CACHE_SIZE = 1000;
    private static final Map<String, Float> toxicityCache = Collections.synchronizedMap(
            new LinkedHashMap<>(MAX_CACHE_SIZE, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, Float> eldest) {
                    return size() > MAX_CACHE_SIZE;
                }
            });

    public float getToxicityScore(String sentence)
    {
        long start = System.nanoTime();

        if (sentence == null || sentence.trim().isEmpty())
        {
            return DEFAULT_TOXICITY_SCORE;
        }

        Float cachedScore = toxicityCache.get(sentence);
        if (cachedScore != null)
        {
            return cachedScore;
        }

        float score = calculateToxicityScore(sentence);

        synchronized (toxicityCache)
        {
            toxicityCache.put(sentence, score);
        }

        long end = System.nanoTime();
        System.out.println("Time of toxicity assessment: " + (end - start) / 1_000_000 + " ms");

        return score;
    }

    private float calculateToxicityScore(String sentence)
    {
        try
        {
            Document doc = Document.newBuilder().setContent(sentence).setType(Type.PLAIN_TEXT).build();

            ModerateTextResponse response = language.moderateText(doc);

            for (ClassificationCategory category : response.getModerationCategoriesList())
            {
                if (TOXIC_CATEGORIES.contains(category.getName()))
                {
                    return category.getConfidence();
                }
            }
        }
        catch (Exception e)
        {
            Logger.getLogger(SentenceAnalyzer.class.getName()).log(Level.WARNING, "Error calculating toxicity score", e);
        }

        return DEFAULT_TOXICITY_SCORE;
    }

    public SentenceAnalyzer() throws IOException
    {
        Properties props = loadConfiguration();
        String credPath = getCredentialsPath(props);
        GoogleCredentials credentials = loadCredentials(credPath);
        this.language = createLanguageClient(credentials);
    }

    private Properties loadConfiguration() throws IOException
    {
        Properties props = new Properties();

        try (FileInputStream fis = new FileInputStream(CONFIG_FILE))
        {
            props.load(fis);
            return props;
        }
        catch (IOException e)
        {
            LOGGER.log(Level.SEVERE, "Configuration file not found or unreadable", e);
            throw new IOException("Configuration file " + CONFIG_FILE + " not found or unreadable", e);
        }
    }

    private String getCredentialsPath(Properties props) throws IOException
    {
        String os = System.getProperty("os.name").toLowerCase();
        String credPath = os.contains("win") ? props.getProperty("windows.key") : os.contains("mac") ? props.getProperty("macos.key") : null;

        if (credPath == null)
        {
            String errorMsg = "Key path not configured for OS: " + os;
            LOGGER.severe(errorMsg);
            throw new IOException(errorMsg);
        }
        return credPath;
    }

    private GoogleCredentials loadCredentials(String credPath) throws IOException
    {
        try (FileInputStream credStream = new FileInputStream(credPath))
        {
            return GoogleCredentials.fromStream(credStream)
                    .createScoped(LanguageServiceSettings.getDefaultServiceScopes());
        }
        catch (IOException e)
        {
            LOGGER.log(Level.SEVERE, "Unable to find or read credentials file", e);
            throw new IOException("Unable to find or read credentials file at: " + credPath, e);
        }
    }

    private LanguageServiceClient createLanguageClient(GoogleCredentials credentials) throws IOException
    {
        LanguageServiceSettings settings = LanguageServiceSettings.newBuilder().setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build();
        return LanguageServiceClient.create(settings);
    }

    public AnalyzeSyntaxResponse analyzeSentence(String sentence)
    {
        long start = System.nanoTime();

        Document doc = createDocument(sentence);
        AnalyzeSyntaxResponse pip;
        pip = language.analyzeSyntax(doc);

        long end = System.nanoTime();
        System.out.println("Time syntactic analysis: " + (end - start) / 1_000_000 + " ms");

        return pip;
    }

    public Map<String, Set<String>> extractWordsByType(AnalyzeSyntaxResponse response)
    {
        Map<String, Set<String>> dictionary = initializeDictionary();

        response.getTokensList().forEach(token -> {
            String word = token.getText().getContent().toLowerCase();
            String pos = token.getPartOfSpeech().getTag().name();

            switch (pos)
            {
                case "NOUN" -> dictionary.get("NOUN").add(word);
                case "VERB" -> dictionary.get("VERB").add(word);
                case "ADJ" -> dictionary.get("ADJ").add(word);
            }
        });

        return dictionary;
    }

    private Map<String, Set<String>> initializeDictionary()
    {
        return Map.of("NOUN", new HashSet<>(), "VERB", new HashSet<>(), "ADJ", new HashSet<>());
    }

    public void saveWordsToDictionaries(Map<String, Set<String>> words) throws IOException
    {
        saveDictionary("nouns.txt", words.get("NOUN"));
        saveDictionary("verbs.txt", words.get("VERB"));
        saveDictionary("adjectives.txt", words.get("ADJ"));
    }

    private void saveDictionary(String fileName, Set<String> newWords) throws IOException
    {
        if (newWords == null || newWords.isEmpty())
        {
            return;
        }

        File file = new File(DICTIONARIES_DIR + fileName);
        ensureFileExists(file);

        Set<String> existingWords = loadDictionary(file);
        List<String> wordsToAppend = filterNewWords(newWords, existingWords);

        if (!wordsToAppend.isEmpty())
        {
            appendToFile(file, wordsToAppend);
        }
    }

    private void ensureFileExists(File file) throws IOException
    {
        if (!file.exists())
        {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
    }

    private List<String> filterNewWords(Set<String> newWords, Set<String> existingWords)
    {
        return newWords.stream()
                .filter(word -> !existingWords.contains(word))
                .collect(Collectors.toList());
    }

    private void appendToFile(File file, List<String> words) throws IOException
    {
        try (FileWriter fw = new FileWriter(file, true))
        {
            for (String word : words)
            {
                fw.write(word + System.lineSeparator());
            }
        }
    }

    private Set<String> loadDictionary(File file) throws IOException
    {
        if (!file.exists())
        {
            return new HashSet<>();
        }

        try (Scanner scanner = new Scanner(file))
        {
            Set<String> words = new HashSet<>();
            while (scanner.hasNextLine())
            {
                words.add(scanner.nextLine().trim().toLowerCase());
            }
            return words;
        }
    }

    public void printSyntaxTree(AnalyzeSyntaxResponse response)
    {
        response.getTokensList().forEach(token ->
                LOGGER.info(String.format(
                        "Word: %-10s Lemma: %-10s Depends on: %d  Type: %s",
                        token.getText().getContent(),
                        token.getLemma(),
                        token.getDependencyEdge().getHeadTokenIndex(),
                        token.getDependencyEdge().getLabel()
                ))
        );
    }

    public boolean isEnglish(String text)
    {
        Document doc = createDocument(text);
        AnalyzeEntitiesResponse response = language.analyzeEntities(AnalyzeEntitiesRequest.newBuilder().setDocument(doc).setEncodingType(EncodingType.UTF8).build());

        return ENGLISH_LANGUAGE_CODE.equals(response.getLanguage());
    }

    private Document createDocument(String content)
    {
        return Document.newBuilder().setContent(content).setType(Type.PLAIN_TEXT).build();
    }

    @Override
    public void close()
    {
        if (language != null)
        {
            try
            {
                language.close();
            }
            catch (Exception e)
            {
                LOGGER.log(Level.WARNING, "Error closing LanguageServiceClient", e);
            }
        }
    }
}