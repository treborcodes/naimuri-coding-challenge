package com.wordsquare;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link WordSquareSolver}.
 *
 * These tests use a stub {@link Dictionary} built from a small inline word
 * list so they run fast and require no network or file access.
 */
@DisplayName("WordSquareSolver")
class WordSquareSolverTest {

    /*
    * Helper to create a dictionary from a list of words without needing a file.
    */
    private static Dictionary dictionaryOf(String... words) {
        return StubDictionary.of(words);
    }

    @Test
    @DisplayName("solves the example 4x4 square from the brief")
    void solve_4x4_exampleFromBrief() {
        Dictionary dict = dictionaryOf("rose", "oven", "send", "ends");
        WordSquareSolver solver = new WordSquareSolver(dict);

        Optional<WordSquare> result = solver.solve(4, "eeeeddoonnnsssrv");

        assertTrue(result.isPresent(), "Expected a solution to be found");
        WordSquare square = result.get();
        assertTrue(square.isComplete());
        assertEquals(4, square.words().size());
        verifySymmetry(square);
    }

    @Test
    @DisplayName("returns empty when no solution exists")
    void solve_noSolution_returnsEmpty() {
        Dictionary dict = dictionaryOf("rose", "oven", "send", "ends");
        WordSquareSolver solver = new WordSquareSolver(dict);

        // Letters that cannot form any valid square
        Optional<WordSquare> result = solver.solve(4, "zzzzzzzzzzzzzzzz");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("throws when letter count does not match grid size")
    void solve_wrongLetterCount_throws() {
        Dictionary dict = dictionaryOf("rose");
        WordSquareSolver solver = new WordSquareSolver(dict);

        assertThrows(IllegalArgumentException.class, () -> solver.solve(4, "abc"));
    }

    @Test
    @DisplayName("solved square satisfies the symmetry constraint")
    void solve_result_isSymmetric() {
        Dictionary dict = dictionaryOf("rose", "oven", "send", "ends");
        WordSquareSolver solver = new WordSquareSolver(dict);

        Optional<WordSquare> result = solver.solve(4, "eeeeddoonnnsssrv");
        result.ifPresent(this::verifySymmetry);
    }

    @Test
    @DisplayName("solution uses exactly the provided letters")
    void solve_usesExactLetters() {
        Dictionary dict = dictionaryOf("rose", "oven", "send", "ends");
        WordSquareSolver solver = new WordSquareSolver(dict);
        String letters = "eeeeddoonnnsssrv";

        Optional<WordSquare> result = solver.solve(4, letters);
        assertTrue(result.isPresent(), "Expected a solution to be found");
        assertEquals(letterFrequency(letters), letterFrequency(String.join("", result.get().words())));
    }

    private static String letterFrequency(String value) {
        return value.chars()
                .sorted()
                .collect(StringBuilder::new,
                        (builder, codePoint) -> builder.append((char) codePoint),
                        StringBuilder::append)
                .toString();
    }

    /** Asserts that word[i].charAt(j) == word[j].charAt(i) for all i, j. */
    private void verifySymmetry(WordSquare square) {
        var words = square.words();
        int n = square.size();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                char rowChar = words.get(i).charAt(j);
                char colChar = words.get(j).charAt(i);
                assertEquals(rowChar, colChar,
                        "Symmetry violated at [%d][%d]: row has '%c', col has '%c'"
                                .formatted(i, j, rowChar, colChar));
            }
        }
    }
}