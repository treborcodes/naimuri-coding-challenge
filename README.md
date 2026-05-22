# Word Square Solver

Solves word-square puzzles: given a grid size *n* and exactly *n²* letters,
arranges those letters into an *n×n* grid where every row and its matching
column spell the same valid English word.

## Requirements

- Java 17+
- Gradle 8+ (or use the included wrapper: `./gradlew.bat` on Windows)
- Internet access on first run to download the dictionary, **or** a local copy
  of [enable1.txt](https://norvig.com/ngrams/enable1.txt)

## Running

```bash
# Using the Gradle wrapper (compiles and runs in one step)
./gradlew.bat run --args="4 eeeeddoonnnsssrv"

# Or build a standalone fat jar first
./gradlew.bat jar
java -jar app/build/libs/wordsquare-1.0.0.jar 4 eeeeddoonnnsssrv

# With a local dictionary file (skips network download)
java -jar app/build/libs/wordsquare-1.0.0.jar 4 eeeeddoonnnsssrv /path/to/enable1.txt
```

The CLI accepts:

1. `size` — the square dimension
2. `letters` — exactly `size * size` letters
3. optional `enable1.txt` path — if omitted, the cached dictionary is used or
   downloaded on first run

### Challenge inputs from the brief

```bash
./gradlew.bat run --args="4 aaccdeeeemmnnnoo"
./gradlew.bat run --args="5 aaaeeeefhhmoonssrrrrttttw"
./gradlew.bat run --args="5 aabbeeeeeeeehmosrrrruttvv"
./gradlew.bat run --args="7 aaaaaaaaabbeeeeeeedddddggmmlloooonnssssrrrruvvyyy"
```

### Expected output (example)

```
rose
oven
send
ends
```

## Running the tests

```bash
./gradlew.bat test

# With console output
./gradlew.bat test --info
```

## Dictionary

On first run the solver downloads `enable1.txt` (~1.8 MB, ~172k words) from
`https://norvig.com/ngrams/enable1.txt` and caches it at
`~/.wordsquare/enable1.txt`.

Subsequent runs use the cached file if available. If a local dictionary path is
provided as the third CLI argument, that file is loaded instead.

The dictionary loader only keeps words of the requested length and precomputes
all prefixes for fast `isPrefix` checks.

---

## Design notes

### No 2-D arrays

The grid is represented as a `List<String>` — one string per row. This works
because the word-square constraint means **row *i* == column *i***: the list of
words *is* the grid. This makes the model simple to reason about and easy to test.

### Class overview

| Class | Responsibility |
|---|---|
| `Dictionary` | Loads the word list, filters words by length, and stores word/prefix sets |
| `StubDictionary` | Test helper that provides an in-memory dictionary without file/network access |
| `WordSquare` | Mutable partial or complete square represented as a list of row words |
| `WordSquareSolver` | Backtracking search with prefix pruning and available-letter filtering |
| `Main` | CLI entry point and application driver |

### Algorithm

1. Start with an empty `WordSquare`.
2. For the next row, compute the required prefix from already placed words.
3. Enumerate dictionary words that match the prefix and can be formed from the
   remaining letters.
4. Recurse and backtrack when no candidates remain.

The solver prunes the search space aggressively by validating prefixes early,
so even the 7×7 puzzle completes quickly with the current dictionary.