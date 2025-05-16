package it.unipd.nonsense.utils;

import com.google.cloud.language.v1.*;
import com.google.cloud.language.v1.Document.Type;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class SentenceAnalyzer {

    private static final String DIZIONARI_DIR = "src/resources/dictionaries/";
    private final LanguageServiceClient language;

    public SentenceAnalyzer() throws IOException {
        this.language = LanguageServiceClient.create();
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
