// Adjective.java
package it.unipd.nonsense.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Adjective extends Words {
    public Adjective() throws IOException {
        super("dictionaries/adjectives.txt");
    }
}