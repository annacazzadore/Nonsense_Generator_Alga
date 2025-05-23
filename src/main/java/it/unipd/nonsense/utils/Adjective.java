// Adjective.java
package it.unipd.nonsense.utils;

import java.io.IOException;

public class Adjective extends Words {
    public Adjective() throws IOException {
        super("dictionaries/adjectives.txt");
    }
}