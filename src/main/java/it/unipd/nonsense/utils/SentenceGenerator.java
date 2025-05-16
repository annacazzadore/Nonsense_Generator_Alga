package it.unipd.nonsense.utils;

import java.io.IOException;
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
        //AGGIUNGERE SCELTA DEL TEMPLATE E NON RANDOMICO
        String template = structure.getRandom();
        return template.replace("[noun]", noun.getRandom())
                       .replace("[verb]", verb.getRandom())
                       .replace("[adjective]", adjective.getRandom());
    }
}
