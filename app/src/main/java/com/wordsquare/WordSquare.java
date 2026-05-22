package com.wordsquare;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a (potentially partial) word square as a list of words.
 *
 * A word square of size n is fully described by its list of n
 * words, because the symmetry constraint means row i and column i must be identical.
 * Storing words as a {@code List<String>} rather than a 2-D array keeps the model
 * readable and easy to reason about.
 *
 * Example – a complete 4×4 square:
 *   words = ["rose", "oven", "send", "ends"]
 *
 *   r o s e
 *   o v e n
 *   s e n d
 *   e n d s
 *
 * The solver adds and removes words directly during backtracking, so this
 * class is intentionally mutable.
 */
public class WordSquare {

    private final int size;
    private final List<String> words;

    /**
     * Creates an empty word square of the given size.
     *
     * @param size the number of rows (and columns)
     */
    public WordSquare(int size) {
        this.size = size;
        this.words = new ArrayList<>();
    }

    /**
     * Adds a word as the next row.
     *
     * @param word a word of length {@link #size()}
     */
    public void addWord(String word) {
        words.add(word);
    }

    /**
     * Removes the last word added — used to backtrack during search.
     */
    public void removeLastWord() {
        words.remove(words.size() - 1);
    }

    /**
     * Returns the prefix that any candidate word for row {@code rowIndex} must match.
     *
     * Because row i == column i, the first {@code words.size()} characters of
     * word {@code rowIndex} are already determined by the characters at position
     * {@code rowIndex} in each existing word.
     *
     * @param rowIndex the (zero-based) index of the row being filled
     * @return the constrained prefix for that row
     */
    public String requiredPrefix(int rowIndex) {
        StringBuilder prefix = new StringBuilder();
        for (String word : words) {
            prefix.append(word.charAt(rowIndex));
        }
        return prefix.toString();
    }

    /**
     * Returns the number of rows that have been placed in the square so far.
     *
     * @return the current number of filled rows
     */
    public int filledRows() {
        return words.size();
    }

    /**
     * Returns the dimension of this word square.
     *
     * @return the configured square size
     */
    public int size() {
        return size;
    }

    /**
     * Returns {@code true} when the word square contains exactly {@link #size()} rows.
     *
     * @return whether the square is complete
     */
    public boolean isComplete() {
        return words.size() == size;
    }

    /**
     * Returns an unmodifiable view of the current row words.
     *
     * @return the words placed in the square, from first row to last
     */
    public List<String> words() {
        return List.copyOf(words);
    }

    /**
     * Formats the word square as a multi-line string, one row per line.
     *
     * @return the formatted square
     */
    @Override
    public String toString() {
        return String.join(System.lineSeparator(), words);
    }
}