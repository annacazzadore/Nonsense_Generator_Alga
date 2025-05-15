package it.unipd.nonsense.generator;

import java.util.Random;

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
}
