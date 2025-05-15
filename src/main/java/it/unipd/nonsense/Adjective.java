package it.unipd.nonsense.generator;

import java.util.Random;

public class Adjective {
    private List<String> adjectives;

    public Adjective() throws IOException {
        InputStream input = getClass().getClassLoader().getResourceAsStream("dictionaries/adjectives.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        adjectives = reader.lines().collect(Collectors.toList());
    }

    public String getRandom() {
        Random rand = new Random();
        return noadjectivesuns.get(rand.nextInt(adjectives.size()));
    }
}
