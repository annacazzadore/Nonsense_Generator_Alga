package it.unipd.nonsense.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Noun {
    private List<String> nouns;

    public Noun() throws IOException {
        InputStream input = getClass().getClassLoader().getResourceAsStream("dictionaries/nouns.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        nouns = reader.lines().collect(Collectors.toList());
    }

    public String getRandom() {
        Random rand = new Random();
        return nouns.get(rand.nextInt(nouns.size()));
    }

    private List<String> getRandomNounFromDictionary() throws IOException {
        Noun nounGenerator = new Noun();
        List<String> nouns = new ArrayList<>();
        nouns.add(nounGenerator.getRandom());
        return nouns;
    }
}
