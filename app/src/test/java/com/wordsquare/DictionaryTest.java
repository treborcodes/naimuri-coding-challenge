package com.wordsquare;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Dictionary")
class DictionaryTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("loads only words of the requested length from a file")
    void loadsOnlyRequestedLength() throws IOException {
        // Create a temporary dictionary file containing words of several lengths.
        Path file = tempDir.resolve("words.txt");
        Files.writeString(file, """
                rose
                oven
                send
                ends
                hello
                a
                """);

        // Load only 4-letter words from the temporary file.
        Dictionary dictionary = new Dictionary(4, file);

        assertEquals(4, dictionary.size());
        assertAll(
                () -> assertTrue(dictionary.isWord("rose")),
                () -> assertTrue(dictionary.isWord("OVEN")),
                () -> assertTrue(dictionary.isWord("Send")),
                () -> assertFalse(dictionary.isWord("hello")),
                () -> assertFalse(dictionary.isWord("a"))
        );
    }

    @Test
    @DisplayName("isPrefix is case-insensitive and supports empty prefix")
    void isPrefix_caseInsensitiveAndEmpty() throws IOException {
        // Use a small word list with 4-letter words for prefix validation.
        Path file = tempDir.resolve("words.txt");
        Files.writeString(file, """
                rose
                oven
                send
                ends
                """);

        Dictionary dictionary = new Dictionary(4, file);

        // Confirm prefix lookup is case-insensitive, recognizes valid prefixes,
        // rejects invalid prefixes, and treats the empty string as a prefix.
        assertAll(
                () -> assertTrue(dictionary.isPrefix("r")),
                () -> assertTrue(dictionary.isPrefix("RO")),
                () -> assertTrue(dictionary.isPrefix("sen")),
                () -> assertFalse(dictionary.isPrefix("x")),
                () -> assertTrue(dictionary.isPrefix(""))
        );
    }
}
