package it.unipd.nonsense;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Adjective {
    private List<String> adjectives;

    public Adjective() throws IOException {
        InputStream input = getClass().getClassLoader().getResourceAsStream("dictionaries/adjectives.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        adjectives = reader.lines().collect(Collectors.toList());
    }

    public String getRandom() {
        Random rand = new Random();
        return adjectives.get(rand.nextInt(adjectives.size()));
    }
}
