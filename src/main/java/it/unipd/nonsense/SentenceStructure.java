package it.unipd.nonsense.generator;

import java.util.Random;

public class SentenceStructure {
    private List<String> templates;

    public Noun() throws IOException {
        InputStream input = getClass().getClassLoader().getResourceAsStream("dictionaries/templates.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        templates = reader.lines().collect(Collectors.toList());
    }

    public String getRandom() {
        Random rand = new Random();
        return templates.get(rand.nextInt(Templates.size()));
    }
}
