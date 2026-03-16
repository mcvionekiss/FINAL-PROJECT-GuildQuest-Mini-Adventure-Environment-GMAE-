package guildquest.gmae.adventures.wordbomb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A curated bank of {@link WordChallenge}s used by Word Bomb Duel.
 *
 * <p>
 * The bank is shuffled once on construction; subsequent calls to
 * {@link #next()} cycle through the shuffled list (wrapping around when
 * exhausted) so every word is seen before any word repeats.
 */
public class WordBank {

    private final List<WordChallenge> challenges;
    private int cursor;

    public WordBank() {
        challenges = new ArrayList<>(buildChallenges());
        Collections.shuffle(challenges, new Random());
        cursor = 0;
    }

    /**
     * Returns the next challenge in the shuffled sequence.
     * Wraps and re-shuffles once the list is exhausted.
     */
    public WordChallenge next() {
        if (cursor >= challenges.size()) {
            Collections.shuffle(challenges, new Random());
            cursor = 0;
        }
        return challenges.get(cursor++);
    }

    /** Returns the total number of unique challenges in the bank. */
    public int size() {
        return challenges.size();
    }

    // ---- static word list ----

    private static List<WordChallenge> buildChallenges() {
        return List.of(
                new WordChallenge("quest", "A long or arduous search for something important"),
                new WordChallenge("guild", "An association of craftspeople or merchants sharing a trade"),
                new WordChallenge("realm", "A kingdom or domain ruled by a monarch"),
                new WordChallenge("dragon", "A legendary fire-breathing creature with wings and scales"),
                new WordChallenge("rogue", "A dishonest person, or a stealthy adventure class"),
                new WordChallenge("wizard", "A person who practises magic; a sorcerer"),
                new WordChallenge("potion", "A liquid mixture with magical or medicinal properties"),
                new WordChallenge("dungeon", "An underground prison or dangerous subterranean level in a game"),
                new WordChallenge("artifact", "A handmade object of historical or cultural importance"),
                new WordChallenge("scroll", "A roll of parchment or paper used for writing or spells"),
                new WordChallenge("loot", "Valuable items taken from a defeated enemy or chest"),
                new WordChallenge("tavern", "An establishment that serves food and drink to travellers"),
                new WordChallenge("shield", "A broad piece of armour carried to intercept blows"),
                new WordChallenge("enchant", "To put under a spell; to imbue with magical properties"),
                new WordChallenge("forge", "To shape metal by heating and hammering; a smith's furnace"),
                new WordChallenge("phantom", "A ghost or apparition; something elusive or illusory"),
                new WordChallenge("oracle", "A person or place thought to give wise or prophetic advice"),
                new WordChallenge("valor", "Great courage in the face of danger, especially in battle"),
                new WordChallenge("riddle", "A puzzling question or statement whose answer requires ingenuity"),
                new WordChallenge("crystal", "A clear, transparent mineral used in magic and alchemy"),
                new WordChallenge("sigil", "A symbol believed to have magical power or a unique sign"),
                new WordChallenge("aegis", "Protection or support; originally the shield of a Greek god"),
                new WordChallenge("cipher", "A secret or disguised way of writing; a code"),
                new WordChallenge("mana", "A mystical energy source drawn upon to cast spells"));
    }
}
