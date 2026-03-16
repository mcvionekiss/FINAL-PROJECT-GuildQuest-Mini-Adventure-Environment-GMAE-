package guildquest.gmae.adventures.wordbomb;

import java.util.Objects;

/**
 * A usable item available to players during Word Bomb Duel.
 *
 * <p>Each item has a {@link Type} and an {@link AdventureItemEffect} that
 * describes what happens when the item is used.  Instances are created by
 * {@link WordBombItemFactory}.
 */
public class MiniAdventureItem {

    /**
     * All item types available in Word Bomb Duel.
     *
     * <ul>
     *   <li>{@link #HEALTH_POTION}  – restores 1 lost heart</li>
     *   <li>{@link #STOPWATCH}      – adds {@code +15} turns to the current
     *       challenge timer</li>
     *   <li>{@link #SWAP_CARD}      – discards the current challenge and
     *       issues a brand-new one</li>
     *   <li>{@link #HINT}           – reveals the first letter of the answer
     *       and shows blanks for the rest</li>
     * </ul>
     */
    public enum Type {
        HEALTH_POTION,
        STOPWATCH,
        SWAP_CARD,
        HINT
    }

    private final Type               type;
    private final String             name;
    private final AdventureItemEffect effect;

    public MiniAdventureItem(Type type, String name, AdventureItemEffect effect) {
        this.type   = Objects.requireNonNull(type,   "type cannot be null");
        this.name   = Objects.requireNonNull(name,   "name cannot be null");
        this.effect = Objects.requireNonNull(effect, "effect cannot be null");
    }

    /** The category / behaviour of this item. */
    public Type getType() { return type; }

    /** The display name shown to the player. */
    public String getName() { return name; }

    /** The effect descriptor for this item. */
    public AdventureItemEffect getEffect() { return effect; }

    @Override
    public String toString() {
        return name + " (" + type + "): " + effect.getDescription();
    }
}
