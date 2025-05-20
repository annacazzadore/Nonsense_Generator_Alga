package it.unipd.nonsense.utils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class SentenceGenerator {
    private Noun noun;
    private Verb verb;
    private Adjective adjective;
    private SentenceStructure structure;

    public SentenceGenerator() throws IOException {
        noun = new Noun();
        verb = new Verb();
        adjective = new Adjective();
        structure = new SentenceStructure();
    }

    public List<String> generateSentences(int count, String template,
                                          List<String> nouns,
                                          List<String> verbs,
                                          List<String> adjectives) throws IOException {
        List<String> sentences = new ArrayList<>();
        Random rand = new Random();

        // Verifica parametri di input
        if (count <= 0) {
            throw new IllegalArgumentException("Il numero di frasi deve essere maggiore di 0");
        }

        if (template == null || template.isEmpty() || template.equals("-")) {
            throw new IllegalArgumentException("Seleziona un template valido");
        }

        // Se le liste sono vuote, usa parole dai dizionari
        if (nouns == null || nouns.isEmpty()) {
            nouns = getAllNounsFromDictionary();
        }

        if (verbs == null || verbs.isEmpty()) {
            verbs = getAllVerbsFromDictionary();
        }

        if (adjectives == null || adjectives.isEmpty()) {
            adjectives = getAllAdjectivesFromDictionary();
        }

        for (int i = 0; i < count; i++) {
            String sentence = template;

            // Mescola le liste per randomizzare la selezione
            Collections.shuffle(nouns);
            Collections.shuffle(verbs);
            Collections.shuffle(adjectives);

            // Sostituisci i placeholder con parole casuali
            sentence = replacePlaceholders(sentence, nouns, verbs, adjectives, rand);

            sentences.add(sentence);
        }

        return sentences;
    }

    private String replacePlaceholders(String template, List<String> nouns,
                                       List<String> verbs, List<String> adjectives,
                                       Random rand) {
        String result = template;

        // Crea liste combinate di parole disponibili e parole dai dizionari
        List<String> availableNouns = new ArrayList<>(nouns);
        List<String> availableVerbs = new ArrayList<>(verbs);
        List<String> availableAdjectives = new ArrayList<>(adjectives);

        // Sostituisci tutti i [noun]
        while (result.contains("[noun]")) {
            if (availableNouns.isEmpty()) {
                try {
                    availableNouns.addAll(getRandomNounFromDictionary());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            result = result.replaceFirst("\\[noun\\]",
                    availableNouns.remove(rand.nextInt(availableNouns.size())));
        }

        // Sostituisci tutti i [verb]
        while (result.contains("[verb]")) {
            if (availableVerbs.isEmpty()) {
                try{
                    availableVerbs.addAll(getRandomVerbFromDictionary());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            result = result.replaceFirst("\\[verb\\]",
                    availableVerbs.remove(rand.nextInt(availableVerbs.size())));
        }

        // Sostituisci tutti i [adjective]
        while (result.contains("[adjective]")) {
            if (availableAdjectives.isEmpty()) {
                try {
                    availableAdjectives.addAll(getRandomAdjectiveFromDictionary());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            result = result.replaceFirst("\\[adjective\\]",
                    availableAdjectives.remove(rand.nextInt(availableAdjectives.size())));
        }

        return result;
    }

    private List<String> getAllNounsFromDictionary() throws IOException {
        Noun nounGenerator = new Noun();
        List<String> nouns = new ArrayList<>();
        // Aggiungi più parole per avere abbastanza varietà
        for (int i = 0; i < 10; i++) {
            nouns.add(nounGenerator.getRandom());
        }
        return nouns;
    }

    private List<String> getAllVerbsFromDictionary() throws IOException {
        Verb verbGenerator = new Verb();
        List<String> verbs = new ArrayList<>();
        // Aggiungi più parole per avere abbastanza varietà
        for (int i = 0; i < 10; i++) {
            verbs.add(verbGenerator.getRandom());
        }
        return verbs;
    }

    private List<String> getAllAdjectivesFromDictionary() throws IOException {
        Adjective adjectiveGenerator = new Adjective();
        List<String> adjectives = new ArrayList<>();
        // Aggiungi più parole per avere abbastanza varietà
        for (int i = 0; i < 10; i++) {
            adjectives.add(adjectiveGenerator.getRandom());
        }
        return adjectives;
    }

    private List<String> getRandomVerbFromDictionary() throws IOException {
        Verb verbGenerator = new Verb();
        List<String> verbs = new ArrayList<>();
        verbs.add(verbGenerator.getRandom());
        return verbs;
    }

    private List<String> getRandomAdjectiveFromDictionary() throws IOException {
        Adjective adjectiveGenerator = new Adjective();
        List<String> adjectives = new ArrayList<>();
        adjectives.add(adjectiveGenerator.getRandom());
        return adjectives;
    }


    private List<String> getRandomNounFromDictionary() throws IOException {
        Noun nounGenerator = new Noun();
        List<String> nouns = new ArrayList<>();
        nouns.add(nounGenerator.getRandom());
        return nouns;
    }
}