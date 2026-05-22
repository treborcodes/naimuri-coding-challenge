package com.wordsquare;

import java.util.HashSet;
import java.util.Set;

/**
 * A package-private stub implementation of {@link Dictionary} used by tests.
 *
 * This class provides an in-memory word list, avoiding file or network
 * access during unit tests.
 */
class StubDictionary extends Dictionary {

    private final Set<String> words = new HashSet<>();
    private final Set<String> prefixes = new HashSet<>();

    /**
     * Constructs a stub dictionary from an inline word list.
     *
     * @param wordList the words to include in this dictionary
     */
    private StubDictionary(String[] wordList) {
        super();
        for (String word : wordList) {
            String lower = word.toLowerCase();
            words.add(lower);
            for (int i = 1; i <= lower.length(); i++) {
                prefixes.add(lower.substring(0, i));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWord(String word) {
        return words.contains(word.toLowerCase());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPrefix(String prefix) {
        if (prefix.isEmpty()) return true;
        return prefixes.contains(prefix.toLowerCase());
    }

    /**
     * Creates a stub dictionary from the provided words.
     *
     * @param words the word list to use
     * @return a dictionary implementation for testing
     */
    static Dictionary of(String... words) {
        return new StubDictionary(words);
    }
}