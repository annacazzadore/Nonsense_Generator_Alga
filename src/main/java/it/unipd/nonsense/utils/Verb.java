package it.unipd.nonsense;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Verb {
    private List<String> verbs;

    public Verb() throws IOException {
        InputStream input = getClass().getClassLoader().getResourceAsStream("dictionaries/verbs.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        verbs = reader.lines().collect(Collectors.toList());
    }

    public String getRandom() {
        Random rand = new Random();
        return verbs.get(rand.nextInt(verbs.size()));
    }
}
