package guildquest.gmae.adventures.wordbomb;

/**
 * Marker + description contract for effects applied by items in Word Bomb Duel.
 *
 * <p>Concrete effects are implemented as anonymous classes / lambdas inside
 * {@link WordBombItemFactory}; WordBombDuel dispatches them by querying the
 * item's {@link MiniAdventureItem#getType()}.
 */
public interface AdventureItemEffect {

    /**
     * Returns a short human-readable description of what this effect does
     * when activated (shown to the player in the UI).
     */
    String getDescription();
}
