package it.unipd.nonsense.generator;

import java.util.List;

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

    public String generateSentence() {
        String template = structure.getRandom();
        return template.replace("[noun]", noun.getRandom())
                       .replace("[verb]", verb.getRandom())
                       .replace("[adjective]", adjective.getRandom());
    }
}
