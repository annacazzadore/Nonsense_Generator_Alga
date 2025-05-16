package it.unipd.nonsense;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class SentenceStructure {
    private List<String> templates;

    public SentenceStructure() throws IOException {
        InputStream input = getClass().getClassLoader().getResourceAsStream("dictionaries/templates.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        templates = reader.lines().collect(Collectors.toList());
    }

    public String getRandom() {
        Random rand = new Random();
        return templates.get(rand.nextInt(templates.size()));
    }
}
