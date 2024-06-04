import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * * @author - Roman Balayar
 * @CS251L - Lab 07
 * This Evil trick you by generating 4, 5 or 6 letters words when you enter any word ,
 * it will generate another serete word to tick you make more complicate to guess.
 */



public class EvilHangman implements HangmanInterface {
    private Set<Character> gssltr;
    private int guessesRemaining;
    private List<String> dictionary;
    private List<String> currentWords;

    /**
     * Constructs a new EvilHangman game instance by reading a list of words
     * from a specified file to be used as a dictionary.
     * Initializes the game with a default of 6 guesses.
     *
     * @param dictionaryFile The path to the file containing the dictionary of words.
     */

    public EvilHangman(String dictionaryFile) {
        try {
            dictionary = Files.readAllLines(Paths.get(dictionaryFile));
        } catch (IOException e) {
            e.printStackTrace();
            dictionary = new ArrayList<>();
        }
        initGame(6); // Assume 6 guesses by default
    }

    /**
     * Initializes or resets the game with the specified number of guesses
     * and resets the current word list to the full dictionary.
     *
     * @param guesses The number of allowed incorrect guesses.
     */
    @Override
    public void initGame(int guesses) {
        gssltr = new HashSet<>();
        guessesRemaining = guesses;
        currentWords = new ArrayList<>(dictionary);
    }

    /**
     * Returns the number of guesses remaining before the player loses the game.
     *
     * @return The number of guesses left.
     */
    @Override
    public int getGuessesRemaining() {
        return guessesRemaining;
    }

    /**
     * Retrieves a collection of characters that have been guessed so far.
     *
     * @return A set of guessed letters.
     */
    @Override
    public Collection<Character> getGuessedLetters() {
        return gssltr;
    }

    /**
     * Provides the current state of the puzzle, using the largest possible
     * group of words that match a given pattern,
     * showing guessed letters and blanks for unguessed letters.
     * This method is unique to Evil Hangman as it may manipulate
     * the pattern to maximize difficulty.
     *
     * @return A string representation of the puzzle with guessed letters and blanks.
     */

    @Override
    public String getPuzzle() {
        if (currentWords.isEmpty()) return "";
        Map<String, List<String>> families = new HashMap<>();
        for (String word : currentWords) {
            StringBuilder pattern = new StringBuilder();
            for (char c : word.toCharArray()) {
                pattern.append(gssltr.contains(c) ? c : BLANK);
            }
            families.computeIfAbsent(pattern.toString(), k -> new ArrayList<>()).add(word);
        }
        // Choose the largest family
        Optional<String> largestFamily = families.keySet().stream()
                .max(Comparator.comparingInt(k -> families.get(k).size()));
        return largestFamily.orElse("");
    }

    /**
     * Returns a word from the list of current possible words. In the context of Evil Hangman,
     * this word may change as guesses are made to maximize the difficulty of guessing correctly.
     *
     * @return One of the possible secret words.
     */
    @Override
    public String getSecretWord() {
        return currentWords.isEmpty() ? "" : currentWords.get(0);
    }


    /**
     * Checks if the puzzle is complete, i.e., if all letters in
     * the puzzle have been guessed and no blanks are left.
     * This method considers the most common pattern derived from the player's guesses.
     *
     * @return True if the game is complete, otherwise false.
     */

    @Override
    public boolean isComplete() {
        String puzzle = getPuzzle();
        return !puzzle.contains(String.valueOf(BLANK)) && !puzzle.isEmpty();
    }

    /**
     * Determines whether the game is over, which can occur if the puzzle is
     * complete or if there are no guesses remaining.
     *
     * @return True if the game is over, otherwise false.
     */

    @Override
    public boolean isGameOver() {
        return isComplete() || guessesRemaining <= 0;
    }

    /**
     * Updates the game state based on the player's guess by selecting the
     * largest group of words that fit the guessed pattern.
     * This method manipulates the possible words to maintain a high
     * level of challenge, and adjusts the number of guesses remaining
     * if the guess did not improve the puzzle pattern.
     *
     * @param letter The character guessed by the player.
     * @return True if the guess was correct and the letter is part of the current puzzle pattern, otherwise false.
     */
    @Override
    public boolean updateWithGuess(char letter) {
        if (!gssltr.contains(letter)) {
            gssltr.add(letter);
            String previousPattern = getPuzzle();
            List<String> newWords = new ArrayList<>();
            for (String word : currentWords) {
                StringBuilder pattern = new StringBuilder();
                for (char c : word.toCharArray()) {
                    pattern.append(gssltr.contains(c) ? c : BLANK);
                }
                if (pattern.toString().equals(previousPattern)) {
                    newWords.add(word);
                }
            }
            currentWords = newWords;
            if (previousPattern.indexOf(letter) >= 0) {
                return true;
            } else {
                guessesRemaining--;
                return false;
            }
        }
        return false;
    }
}
