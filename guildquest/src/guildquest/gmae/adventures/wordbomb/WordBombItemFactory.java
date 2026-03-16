package guildquest.gmae.adventures.wordbomb;

/**
 * Factory that creates the four types of {@link MiniAdventureItem} used in
 * Word Bomb Duel.  Each factory method produces a fresh item instance with a
 * pre-configured {@link AdventureItemEffect}.
 *
 * <p>WordBombDuel dispatches the actual game-state mutation based on the
 * returned item's {@link MiniAdventureItem.Type}; the effect stored on the
 * item is used for display purposes only.
 */
public class WordBombItemFactory {

    private WordBombItemFactory() { /* static-only utility */ }

    /**
     * Creates a <em>Health Potion</em> that restores one lost heart when used.
     */
    public static MiniAdventureItem createHealthPotion() {
        AdventureItemEffect effect = () -> "Restores 1 lost heart.";
        return new MiniAdventureItem(MiniAdventureItem.Type.HEALTH_POTION,
                "Health Potion", effect);
    }

    /**
     * Creates a <em>Stopwatch</em> that adds 15 turns to the current
     * challenge timer when used.
     */
    public static MiniAdventureItem createStopwatch() {
        AdventureItemEffect effect = () -> "Adds +15 turns to the guess timer.";
        return new MiniAdventureItem(MiniAdventureItem.Type.STOPWATCH,
                "Stopwatch", effect);
    }

    /**
     * Creates a <em>Swap Card</em> that discards the current challenge and
     * assigns a fresh one when used.
     */
    public static MiniAdventureItem createSwapCard() {
        AdventureItemEffect effect = () -> "Replaces the current challenge with a new one.";
        return new MiniAdventureItem(MiniAdventureItem.Type.SWAP_CARD,
                "Swap Card", effect);
    }

    /**
     * Creates a <em>Hint</em> that reveals the first letter of the answer
     * followed by dashes for the remaining letters when used.
     */
    public static MiniAdventureItem createHint() {
        AdventureItemEffect effect = () -> "Reveals the first letter of the answer.";
        return new MiniAdventureItem(MiniAdventureItem.Type.HINT,
                "Hint", effect);
    }
}
