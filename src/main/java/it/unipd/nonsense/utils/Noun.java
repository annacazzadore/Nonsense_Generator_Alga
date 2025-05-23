// Noun.java
package it.unipd.nonsense.utils;

import java.io.IOException;

public class Noun extends Words {
    public Noun() throws IOException {
        super("dictionaries/nouns.txt");
    }
}