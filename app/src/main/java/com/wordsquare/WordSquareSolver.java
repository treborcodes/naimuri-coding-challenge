package com.wordsquare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Solves the word-square problem using backtracking with prefix pruning.
 *
 * Algorithm
 *   - For the next empty row, compute the prefix already constrained by
 *       previously placed words (since row i == column i).
 *   - Find all dictionary words that match that prefix and can be formed
 *       from the remaining letters.
 *   - Place each candidate and recurse. If no candidate leads to a solution,
 *       remove the word and try the next one (backtrack).
 */
public class WordSquareSolver {

    private final Dictionary dictionary;

    /**
     * Creates a solver that uses the provided dictionary for word and prefix lookups.
     *
     * @param dictionary the dictionary to use during search
     */
    public WordSquareSolver(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    /**
     * Attempts to solve the word square for the given letters.
     *
     * @param size    the dimension of the square
     * @param letters the exact multiset of letters to use (length must equal size²)
     * @return an {@link Optional} containing a solution, or empty if none exists
     */
    public Optional<WordSquare> solve(int size, String letters) {
        if (letters.length() != size * size) {
            throw new IllegalArgumentException(
                    "Expected %d letters for a %d×%d square, got %d"
                            .formatted(size * size, size, size, letters.length()));
        }

        Map<Character, Integer> available = frequencyMap(letters);
        return search(new WordSquare(size), available, size);
    }

    // -------------------------------------------------------------------------
    // Private – recursive backtracking
    // -------------------------------------------------------------------------

    /**
     * Recursively searches for a complete word square by filling one row at a time.
     *
     * @param partial   the current partial square
     * @param remaining the remaining letter counts
     * @param size      the target square dimension
     * @return an optional complete square if a solution exists
     */
    private Optional<WordSquare> search(WordSquare partial, Map<Character, Integer> remaining, int size) {
        if (partial.isComplete()) {
            return Optional.of(partial);
        }

        int rowIndex = partial.filledRows();
        String prefix = partial.requiredPrefix(rowIndex);

        if (!dictionary.isPrefix(prefix)) {
            return Optional.empty();
        }

        List<String> candidates = findCandidates(prefix, remaining, size);
        for (String candidate : candidates) {
            Map<Character, Integer> next = copyMap(remaining);
            subtractInPlace(next, candidate);
            partial.addWord(candidate);
            Optional<WordSquare> result = search(partial, next, size);
            if (result.isPresent()) {
                return result;
            }
            partial.removeLastWord();
        }

        return Optional.empty();
    }

    /**
     * Finds all words of length {@code wordLength} that start with {@code prefix}
     * and can be formed from the {@code available} letters.
     *
     * @param prefix     the prefix the candidate words must begin with
     * @param available  the remaining letter counts
     * @param wordLength the required word length
     * @return a list of candidate words that satisfy both constraints
     */
    private List<String> findCandidates(String prefix,
                                        Map<Character, Integer> available,
                                        int wordLength) {
        List<String> candidates = new ArrayList<>();
        Map<Character, Integer> afterPrefix = copyMap(available);
        if (!subtractInPlace(afterPrefix, prefix)) {
            return candidates;
        }
        buildWords(prefix, afterPrefix, wordLength, candidates);
        return candidates;
    }

    /**
     * Recursively extends {@code current} one character at a time,
     * collecting valid words into {@code results}.
     *
     * @param current    the current candidate prefix being built
     * @param available  the remaining letter counts
     * @param wordLength the target word length
     * @param results    the list to collect matching words into
     */
    private void buildWords(String current,
                            Map<Character, Integer> available,
                            int wordLength,
                            List<String> results) {
        if (current.length() == wordLength) {
            if (dictionary.isWord(current)) {
                results.add(current);
            }
            return;
        }

        for (char ch : sortedAvailableChars(available)) {
            String next = current + ch;
            if (dictionary.isPrefix(next)) {
                available.put(ch, available.get(ch) - 1);
                if (available.get(ch) == 0) {
                    available.remove(ch);
                }
                buildWords(next, available, wordLength, results);
                available.merge(ch, 1, Integer::sum);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Letter-frequency helpers
    // -------------------------------------------------------------------------

    /**
     * Builds a character-frequency map from {@code s}.
     *
     * @param s the source string
     * @return a map from character to remaining count
     */
    private Map<Character, Integer> frequencyMap(String s) {
        Map<Character, Integer> freq = new HashMap<>();
        for (char ch : s.toCharArray()) {
            freq.put(ch, freq.getOrDefault(ch, 0) + 1);
        }
        return freq;
    }

    /**
     * Subtracts the letters of {@code word} from {@code map} in place.
     * Returns {@code false} if any letter is unavailable.
     *
     * @param map  the available letter counts
     * @param word the word to subtract
     * @return true when all letters were successfully subtracted
     */
    private boolean subtractInPlace(Map<Character, Integer> map, String word) {
        for (char ch : word.toCharArray()) {
            int count = map.getOrDefault(ch, 0);
            if (count == 0) {
                return false;
            }
            if (count == 1) {
                map.remove(ch);
            } else {
                map.put(ch, count - 1);
            }
        }
        return true;
    }

    /**
     * Returns a sorted list of characters that still have remaining count.
     *
     * @param available the remaining letter counts
     * @return a sorted list of available characters
     */
    private List<Character> sortedAvailableChars(Map<Character, Integer> available) {
        List<Character> chars = new ArrayList<>(available.keySet());
        chars.sort(null);
        return chars;
    }

    /**
     * Returns a shallow copy of the character-frequency map.
     *
     * @param map the original map to copy
     * @return a new map containing the same entries
     */
    private Map<Character, Integer> copyMap(Map<Character, Integer> map) {
        return new HashMap<>(map);
    }
}