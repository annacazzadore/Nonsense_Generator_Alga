package it.unipd.nonsense.generator;

import java.util.Random;

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
