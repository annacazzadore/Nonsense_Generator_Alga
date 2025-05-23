package it.unipd.nonsense.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public abstract class Words {
    protected List<String> words;
    private final Random rand = new Random();

    public Words(String dictionaryPath) throws IOException {
        InputStream input = getClass().getClassLoader().getResourceAsStream(dictionaryPath);
        if (input == null) {
            throw new IOException("Dictionary file not found: " + dictionaryPath);
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            words = reader.lines().collect(Collectors.toList());
        }
    }

    public String getRandom() {
        return words.get(rand.nextInt(words.size()));
    }

    protected List<String> getRandomFromDictionary(int count) throws IOException {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            result.add(getRandom());
        }
        return result;
    }
}