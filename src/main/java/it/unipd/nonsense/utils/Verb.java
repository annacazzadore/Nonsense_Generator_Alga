// Verb.java
package it.unipd.nonsense.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Verb extends Words {
    public Verb() throws IOException {
        super("dictionaries/verbs.txt");
    }
}