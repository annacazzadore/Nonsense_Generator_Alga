package it.unipd.nonsense.utils;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.language.v1.*;
import com.google.cloud.language.v1.Document.Type;
import com.google.api.gax.core.FixedCredentialsProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class SentenceAnalyzer {

    private static final String DIZIONARI_DIR = "src/resources/dictionaries/";
    private static final String CONFIG_FILE = "config.properties";
    private final LanguageServiceClient language;

    public SentenceAnalyzer() throws IOException {
        // Carica il file di configurazione
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            props.load(fis);
        } catch (IOException e) {
            throw new IOException("File di configurazione " + CONFIG_FILE + " non trovato o illeggibile", e);
        }

        // Determina il percorso delle credenziali in base all'OS
        String os = System.getProperty("os.name").toLowerCase();
        String credPath;

        if (os.contains("win")) {
            credPath = props.getProperty("windows.key");
        } else if (os.contains("mac")) {
            credPath = props.getProperty("macos.key");
        } else {
            throw new RuntimeException("Sistema operativo non supportato: " + os);
        }

        if (credPath == null) {
            throw new IOException("Percorso della chiave non configurato per il sistema operativo: " + os);
        }

        // Carica le credenziali
        GoogleCredentials credentials;
        try
        {
            credentials = GoogleCredentials.fromStream(new FileInputStream(credPath)).createScoped(LanguageServiceSettings.getDefaultServiceScopes());
        }
        catch (IOException e) {
            throw new IOException("Impossibile trovare o leggere il file delle credenziali in: " + credPath, e);
        }

        // Crea il client con le credenziali
        LanguageServiceSettings settings = LanguageServiceSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();
        this.language = LanguageServiceClient.create(settings);
    }

    public AnalyzeSyntaxResponse analyzeSentence(String sentence) throws IOException {
        Document doc = Document.newBuilder()
                .setContent(sentence)
                .setType(Type.PLAIN_TEXT)
                .build();
        return language.analyzeSyntax(doc);
    }

    public Map<String, Set<String>> extractWordsByType(AnalyzeSyntaxResponse response) {
        Map<String, Set<String>> dictionary = new HashMap<>();
        dictionary.put("NOUN", new HashSet<>());
        dictionary.put("VERB", new HashSet<>());
        dictionary.put("ADJ", new HashSet<>());

        for (Token token : response.getTokensList()) {
            String word = token.getText().getContent().toLowerCase();
            String pos = token.getPartOfSpeech().getTag().name();
            switch (pos) {
                case "NOUN":
                    dictionary.get("NOUN").add(word);
                    break;
                case "VERB":
                    dictionary.get("VERB").add(word);
                    break;
                case "ADJ":
                    dictionary.get("ADJ").add(word);
                    break;
            }
        }

        return dictionary;
    }

    public void saveWordsToDictionaries(Map<String, Set<String>> words) throws IOException {
        updateDictionaryFile("nouns.txt", words.get("NOUN"));
        updateDictionaryFile("verbs.txt", words.get("VERB"));
        updateDictionaryFile("adjectives.txt", words.get("ADJ"));
    }

    private void updateDictionaryFile(String fileName, Set<String> newWords) throws IOException {
        File file = new File(DIZIONARI_DIR + fileName);

        if (!file.exists()) {
            file.getParentFile().mkdirs(); // crea la cartella se non esiste
            file.createNewFile();
        }

        Set<String> existingWords = loadDictionary(file);
        List<String> toAppend = new ArrayList<>();

        for (String word : newWords) {
            if (!existingWords.contains(word)) {
                toAppend.add(word);
            }
        }

        if (!toAppend.isEmpty()) {
            try (FileWriter fw = new FileWriter(file, true)) {
                for (String word : toAppend) {
                    fw.write(word + System.lineSeparator());
                }
            }
        }
    }

    private Set<String> loadDictionary(File file) throws IOException {
        Set<String> words = new HashSet<>();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                words.add(scanner.nextLine().trim().toLowerCase());
            }
        }
        return words;
    }

    public void printSyntaxTree(AnalyzeSyntaxResponse response) {
        for (Token token : response.getTokensList()) {
            System.out.printf("Parola: %-10s Lemma: %-10s Dipende da: %d  Tipo: %s%n",
                    token.getText().getContent(),
                    token.getLemma(),
                    token.getDependencyEdge().getHeadTokenIndex(),
                    token.getDependencyEdge().getLabel());
        }
    }

    public void close() throws Exception {
        language.close();
    }
}
