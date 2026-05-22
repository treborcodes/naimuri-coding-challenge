package com.wordsquare;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WordSquare")
class WordSquareTest {

    @Test
    @DisplayName("empty square has zero filled rows and is not complete")
    void emptySquare_isNotComplete() {
        // New squares start empty and should not be considered complete.
        WordSquare square = new WordSquare(4);
        assertEquals(0, square.filledRows());
        assertFalse(square.isComplete());
    }

    @Test
    @DisplayName("requiredPrefix is empty for the first row")
    void requiredPrefix_firstRow_isEmpty() {
        // With no rows placed yet, the first row can start with any letter.
        WordSquare square = new WordSquare(4);
        assertEquals("", square.requiredPrefix(0));
    }

    @Test
    @DisplayName("requiredPrefix reflects column characters from placed words")
    void requiredPrefix_derivedFromColumns() {
        // Placing words determines the prefix of the next row in the column.
        WordSquare square = new WordSquare(4);
        square.addWord("rose");
        assertEquals("o", square.requiredPrefix(1));

        square.addWord("oven");
        assertEquals("se", square.requiredPrefix(2));
    }

    @Test
    @DisplayName("removeLastWord rolls back the most recently added word")
    void removeLastWord_rollsBack() {
        // Backtracking should remove only the last placed row.
        WordSquare square = new WordSquare(4);
        square.addWord("rose");
        square.addWord("oven");
        square.removeLastWord();

        assertEquals(1, square.filledRows());
        assertEquals("rose", square.words().get(0));
    }

    @Test
    @DisplayName("isComplete returns true when all rows are filled")
    void isComplete_allRowsFilled() {
        // A square is complete when it contains exactly size rows.
        WordSquare square = new WordSquare(4);
        square.addWord("rose");
        square.addWord("oven");
        square.addWord("send");
        square.addWord("ends");

        assertTrue(square.isComplete());
        assertEquals(4, square.filledRows());
    }

    @Test
    @DisplayName("toString produces one word per line")
    void toString_formatsCorrectly() {
        // The string representation should join the row words with line separators.
        WordSquare square = new WordSquare(4);
        square.addWord("rose");
        square.addWord("oven");
        square.addWord("send");
        square.addWord("ends");

        String[] lines = square.toString().split(System.lineSeparator());
        assertArrayEquals(new String[]{"rose", "oven", "send", "ends"}, lines);
    }

    @Test
    @DisplayName("words returns an unmodifiable view")
    void words_returnsUnmodifiableView() {
        // The internal word list must not be modifiable through the public view.
        WordSquare square = new WordSquare(4);
        square.addWord("rose");

        assertThrows(UnsupportedOperationException.class, () -> square.words().add("oven"));
        assertEquals(1, square.words().size());
    }
}
