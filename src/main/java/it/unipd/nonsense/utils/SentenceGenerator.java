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

    public List<String> generateSentences(int count, String template, List<String> nouns, List<String> verbs, List<String> adjectives) throws IOException {
        long start = System.nanoTime();

        List<String> sentences = new ArrayList<>();
        Random rand = new Random();

        // Verifica parametri di input
        if (count <= 0) {
            throw new IllegalArgumentException("Il numero di frasi deve essere maggiore di 0");
        }

        if (template == null || template.isEmpty() || template.equals("-")) {
            throw new IllegalArgumentException("Seleziona un template valido");
        }

        for (int i = 0; i < count; i++) {
            String sentence = template;

            // Per ogni frase, ottieni nuove parole dai dizionari se le liste fornite sono vuote
            List<String> currentNouns = (nouns == null || nouns.isEmpty()) ? getAllNounsFromDictionary() : new ArrayList<>(nouns);
            List<String> currentVerbs = (verbs == null || verbs.isEmpty()) ? getAllVerbsFromDictionary() : new ArrayList<>(verbs);
            List<String> currentAdjectives = (adjectives == null || adjectives.isEmpty()) ? getAllAdjectivesFromDictionary() : new ArrayList<>(adjectives);

            // Mescola le liste per randomizzare la selezione
            Collections.shuffle(currentNouns, rand);
            Collections.shuffle(currentVerbs, rand);
            Collections.shuffle(currentAdjectives, rand);

            // Sostituisci i placeholder con parole casuali
            sentence = replacePlaceholders(sentence, currentNouns, currentVerbs, currentAdjectives, rand);

            sentences.add(sentence);
        }
        long end = System.nanoTime();
        System.out.println("Tempo generazione frasi (" + count + "): " + (end - start) / 1_000_000 + " ms");

        return sentences;
    }
    
    private String replacePlaceholders(String template, List<String> nouns, List<String> verbs, List<String> adjectives, Random rand) {
        String result = template;

        // Conta quanti placeholder servono
        int nounCount = countPlaceholders(result, "\\[noun\\]");
        int verbCount = countPlaceholders(result, "\\[verb\\]");
        int adjCount = countPlaceholders(result, "\\[adjective\\]");

        // Completa le liste se servono più parole
        try {
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

        // Prendi solo il numero giusto di parole e mescola
        List<String> nounPool = new ArrayList<>(nouns.subList(0, nounCount));
        List<String> verbPool = new ArrayList<>(verbs.subList(0, verbCount));
        List<String> adjPool = new ArrayList<>(adjectives.subList(0, adjCount));

        Collections.shuffle(nounPool, rand);
        Collections.shuffle(verbPool, rand);
        Collections.shuffle(adjPool, rand);

        // Sostituisci
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

    private int countPlaceholders(String text, String placeholderRegex) {
        return text.split(placeholderRegex, -1).length - 1;
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





}