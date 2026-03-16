package guildquest.gmae.adventures.wordbomb;

import java.util.Objects;

/**
 * A single round challenge used in Word Bomb Duel.
 * Each challenge pairs a secret {@link #word} with a {@link #definition}
 * shown to the players. Comparison is case-insensitive and trims whitespace.
 */
public class WordChallenge {

    private final String word;
    private final String definition;

    public WordChallenge(String word, String definition) {
        this.word = Objects.requireNonNull(word, "word cannot be null").toLowerCase().trim();
        this.definition = Objects.requireNonNull(definition, "definition cannot be null");
    }

    /** The correct answer (stored lower-case). */
    public String getWord() {
        return word;
    }

    /** The clue shown to both players. */
    public String getDefinition() {
        return definition;
    }

    /**
     * Returns {@code true} if {@code guess} matches the word
     * (case-insensitive, leading/trailing whitespace ignored).
     */
    public boolean isCorrect(String guess) {
        if (guess == null)
            return false;
        return word.equalsIgnoreCase(guess.trim());
    }

    /** A hint revealing only the first letter followed by underscores. */
    public String getHint() {
        if (word.isEmpty())
            return "";
        return word.charAt(0) + "_".repeat(word.length() - 1);
    }

    @Override
    public String toString() {
        return "Definition: " + definition;
    }
}
