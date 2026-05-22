package com.wordsquare;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Loads an English-language word list and provides fast lookup operations for
 * exact words and prefixes.
 *
 * Only words matching the requested length are retained, which keeps memory
 * usage low. All valid prefixes for the loaded words are precomputed so
 * {@link #isPrefix(String)} checks are effectively O(1).
 */
public class Dictionary {
    private static final String ENABLE1_URL = "https://norvig.com/ngrams/enable1.txt";
    private static final Path CACHE_PATH = Path.of(System.getProperty("user.dir"), ".wordsquare", "enable1.txt");

    private final Set<String> words = new HashSet<>();
    private final Set<String> prefixes = new HashSet<>();

    /**
     * Protected constructor used by test subclasses such as {@link StubDictionary}.
     */
    protected Dictionary() {
        // For testing with StubDictionary
    }

    /**
     * Loads dictionary words of the requested length using the default source.
     *
     * @param wordLength the length of words to include
     * @throws IOException if the dictionary cannot be loaded
     */
    public Dictionary(int wordLength) throws IOException {
        this(wordLength, null);
    }

    /**
     * Loads dictionary words of the requested length from the given file path.
     *
     * If {@code filePath} is null, the cached dictionary file is used if
     * available, otherwise the dictionary is downloaded from the configured URL.
     *
     * @param wordLength the length of words to include
     * @param filePath   optional path to a local dictionary file
     * @throws IOException if the dictionary cannot be loaded
     */
    public Dictionary(int wordLength, Path filePath) throws IOException {
        try (BufferedReader reader = readerFor(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String word = line.trim().toLowerCase();
                if (word.length() == wordLength) {
                    words.add(word);
                    for (int i = 1; i <= word.length(); i++) {
                        prefixes.add(word.substring(0, i));
                    }
                }
            }
        }
    }

    /**
     * Returns the number of words loaded into this dictionary.
     *
     * @return the number of loaded words
     */
    public int size() {
        return words.size();
    }

    /**
     * Returns true when the supplied string is an exact word in this dictionary.
     *
     * @param word the word to test
     * @return true if the lowercased word is present in the dictionary
     */
    public boolean isWord(String word) {
        return words.contains(word.toLowerCase());
    }

    /**
     * Returns true when the supplied string is a prefix of any loaded word.
     *
     * @param prefix the prefix to test
     * @return true when the prefix is empty or exists among precomputed prefixes
     */
    public boolean isPrefix(String prefix) {
        if (prefix.isEmpty()) {
            return true;
        }
        return prefixes.contains(prefix.toLowerCase());
    }

    /**
     * Returns a buffered reader for the active dictionary source.
     *
     * @param filePath optional local dictionary file path
     * @return a reader for the dictionary content
     * @throws IOException if the dictionary cannot be opened or downloaded
     */
    private BufferedReader readerFor(Path filePath) throws IOException {
        if (filePath != null && Files.exists(filePath)) {
            return Files.newBufferedReader(filePath);
        }

        if (CACHE_PATH.getParent() != null) {
            Files.createDirectories(CACHE_PATH.getParent());
        }

        if (Files.exists(CACHE_PATH)) {
            return Files.newBufferedReader(CACHE_PATH);
        }

        return new BufferedReader(new InputStreamReader(download()));
    }

    /**
     * Downloads the dictionary from the configured remote source and caches it.
     *
     * @return an input stream for the cached dictionary file
     * @throws IOException if the download fails or is interrupted
     */
    private InputStream download() throws IOException {
        try {
            System.out.println("Downloading dictionary...");
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ENABLE1_URL))
                    .build();
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            Files.createDirectories(CACHE_PATH.getParent());
            Files.write(CACHE_PATH, response.body());
            System.out.println("Dictionary cached to " + CACHE_PATH);

            return Files.newInputStream(CACHE_PATH);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Dictionary download interrupted", e);
        }
    }
}