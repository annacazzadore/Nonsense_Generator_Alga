// Noun.java
package it.unipd.nonsense.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Noun extends Words {
    public Noun() throws IOException {
        super("dictionaries/nouns.txt");
    }
}