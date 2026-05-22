package com.wordsquare;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Command-line entry point for the Word Square solver.
 *
 * This class parses CLI arguments, loads the appropriate dictionary, and
 * runs the solver for the requested square size and letters.
 *
 * Usage:
 * {@code java -jar wordsquare-1.0.0.jar <size> <letters> [dictionaryPath]}
 */
public class Main {

    /**
     * Application entry point.
     *
     * @param args the command-line arguments: size, letters, and an optional dictionary file path
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: wordsquare <size> <letters>");
            System.err.println("Example: wordsquare 4 eeeeddoonnnsssrv");
            System.exit(1);
        }

        int size;
        try {
            size = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("Error: size must be an integer, got: " + args[0]);
            System.exit(1);
            return;
        }

        String letters = args[1].toLowerCase();
        int expected = size * size;
        if (letters.length() != expected) {
            System.err.printf("Error: expected %d letters for a %dx%d square, got %d%n", expected, size, size, letters.length());
            System.exit(1);
        }

        // Optional dictionary path argument
        Path dictionaryPath = args.length >= 3 ? Path.of(args[2]) : null;

        try {
            System.out.printf("Solving %dx%d word square for letters: %s%n", size, size, letters);

            Dictionary dictionary = new Dictionary(size, dictionaryPath);
            WordSquareSolver solver = new WordSquareSolver(dictionary);

            System.out.println("Dictionary size: " + dictionary.size());
            Optional<WordSquare> solution = solver.solve(size, letters);

            if (solution.isPresent()) {
                System.out.println();
                System.out.println(solution.get());
            } else {
                System.out.println("No solution found for the given letters.");
                System.exit(2);
            }

        } catch (IOException e) {
            System.err.println("Error loading dictionary: " + e.getMessage());
            System.exit(1);
        }
    }
}