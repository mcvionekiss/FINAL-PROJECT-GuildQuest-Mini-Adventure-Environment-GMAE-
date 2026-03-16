package guildquest.gmae.adventures.wordbomb;

import guildquest.gmae.engine.AbstractMiniAdventure;
import guildquest.gmae.enums.GameResult;
import guildquest.gmae.enums.InputType;
import guildquest.gmae.enums.PlayerSlot;

/**
 * Word Bomb Duel -- a two-player word-guessing mini-adventure.
 *
 * <h2>Rules</h2>
 * <ul>
 * <li>Both players share the same active {@link WordChallenge} (a word shown
 * as a definition; players must type the matching word).</li>
 * <li>Each challenge has a {@value #GUESS_TIMER_LIMIT}-turn timer. When the
 * timer reaches zero every player who has <em>not</em> yet answered
 * correctly loses one heart and the challenge advances.</li>
 * <li>Correct guesses award +1 to that player's score without ending the
 * challenge early for the other player.</li>
 * <li>The match runs for {@value #MAX_TURNS} turns (approx 5 minutes at 1
 * turn/s).
 * At the end:
 * <ol>
 * <li>Higher <em>score</em> (correct guesses) wins.</li>
 * <li>If scores are tied, more <em>hearts</em> wins.</li>
 * <li>If both are equal, the result is a <em>draw</em>.</li>
 * </ol>
 * </li>
 * </ul>
 *
 * <h2>Items</h2>
 * Each player starts with one of every item type. Type one of these commands
 * instead of guessing to use an item:
 * <ul>
 * <li>{@code USE POTION} -- restore 1 lost heart (max
 * {@value #MAX_HEARTS})</li>
 * <li>{@code USE STOP} -- add +15 turns to the current challenge timer</li>
 * <li>{@code USE SWAP} -- discard this challenge; get a fresh one</li>
 * <li>{@code USE HINT} -- reveal the first letter of the answer</li>
 * </ul>
 *
 * <h2>Input</h2>
 * All input arrives via {@link InputType#TEXT}. The engine alternates turns
 * between P1 and P2; on each turn the active player either guesses or uses an
 * item. The shared challenge timer decrements every {@code onTick()} call.
 */
public class WordBombDuel extends AbstractMiniAdventure {

    // ---- timing constants (1 turn = 1 second) ----
    /** Total match length (5 minutes). */
    private static final int MAX_TURNS = 300;
    /**
     * Turns allowed per challenge (60 turns). Both players share this window
     * (they alternate turns so each gets approx 30 of their own turns).
     */
    private static final int GUESS_TIMER_LIMIT = 60;
    /** Time bonus added by the Stopwatch item. */
    private static final int STOPWATCH_BONUS = 15;
    /** Starting hearts per player. */
    private static final int MAX_HEARTS = 3;

    // ---- shared challenge state ----
    private WordBank wordBank;
    private WordChallenge currentChallenge;
    /** Counts down from GUESS_TIMER_LIMIT; challenge expires at 0. */
    private int challengeTimer;
    private boolean p1AnsweredCorrectly;
    private boolean p2AnsweredCorrectly;
    private String sharedHint; // non-null once hint used

    // ---- per-player state ----
    private int p1Hearts, p2Hearts;
    private int p1Potions, p2Potions;
    private int p1Stops, p2Stops;
    private int p1Swaps, p2Swaps;
    private int p1Hints, p2Hints;

    // ================================================================
    // Lifecycle
    // ================================================================

    @Override
    protected void onStart() {
        wordBank = new WordBank();

        // scores (state.player1Score / player2Score = correct guesses)
        state.setPlayer1Score(0);
        state.setPlayer2Score(0);

        // hearts
        p1Hearts = MAX_HEARTS;
        p2Hearts = MAX_HEARTS;

        // item counts (each player starts with 1 of every type, 2 hints)
        p1Potions = p2Potions = 1;
        p1Stops = p2Stops = 1;
        p1Swaps = p2Swaps = 1;
        p1Hints = p2Hints = 2;

        // first challenge
        assignNewChallenge();

        String realmName = (realm == null) ? "Unknown Realm" : realm.getName();
        String worldTime = (clock == null) ? "Unknown Time" : clock.now().toString();

        state.setMessage(
                "Word Bomb Duel started in " + realmName + " at " + worldTime
                        + "\nGuess the word from its definition, or type:"
                        + "\n  USE POTION | USE STOP | USE SWAP | USE HINT");
        state.setBoardSnapshot(buildSnapshot());
    }

    @Override
    protected void onInput(PlayerSlot slot, InputType inputType, String payload) {
        if (payload == null || payload.isBlank()) {
            state.setMessage(slot + ": empty input ignored. Type your guess or USE <item>.");
            return;
        }

        String input = payload.trim();

        // ---- item use ----
        if (input.toUpperCase().startsWith("USE ")) {
            handleItemUse(slot, input.substring(4).trim().toUpperCase());
            return;
        }

        // ---- word guess ----
        handleGuess(slot, input);
    }

    @Override
    protected void onTick() {
        // decrement shared challenge timer
        challengeTimer--;

        if (challengeTimer <= 0) {
            // penalise players who did not answer correctly
            StringBuilder penalty = new StringBuilder(
                    "[TIME] Time's up for this challenge! Answer was: \""
                            + currentChallenge.getWord() + "\"");

            if (!p1AnsweredCorrectly) {
                p1Hearts = Math.max(0, p1Hearts - 1);
                penalty.append("\n  ").append(player1.getPlayerName())
                        .append(" loses a heart! (").append(p1Hearts).append(" left)");
            }
            if (!p2AnsweredCorrectly) {
                p2Hearts = Math.max(0, p2Hearts - 1);
                penalty.append("\n  ").append(player2.getPlayerName())
                        .append(" loses a heart! (").append(p2Hearts).append(" left)");
            }

            state.setMessage(penalty.toString());
            assignNewChallenge();
        }

        state.setBoardSnapshot(buildSnapshot());
    }

    @Override
    protected GameResult evaluateWinCondition() {
        // early-exit: a player losing all hearts means immediate loss
        if (p1Hearts <= 0 && p2Hearts <= 0) {
            state.setMessage("Both players have run out of hearts -- it's a draw!");
            return GameResult.DRAW;
        }
        if (p1Hearts <= 0) {
            state.setMessage(player1.getPlayerName() + " ran out of hearts!");
            return GameResult.PLAYER2_WIN;
        }
        if (p2Hearts <= 0) {
            state.setMessage(player2.getPlayerName() + " ran out of hearts!");
            return GameResult.PLAYER1_WIN;
        }

        // time limit reached
        if (state.getTurnNumber() >= MAX_TURNS) {
            return resolveByScore();
        }

        return GameResult.ONGOING;
    }

    // ================================================================
    // Private helpers
    // ================================================================

    /** Assigns a fresh challenge from the word bank and resets per-round state. */
    private void assignNewChallenge() {
        currentChallenge = wordBank.next();
        challengeTimer = GUESS_TIMER_LIMIT;
        p1AnsweredCorrectly = false;
        p2AnsweredCorrectly = false;
        sharedHint = null;
    }

    /** Processes a word-guess input from the given player. */
    private void handleGuess(PlayerSlot slot, String guess) {
        boolean alreadyAnswered = (slot == PlayerSlot.P1) ? p1AnsweredCorrectly : p2AnsweredCorrectly;
        if (alreadyAnswered) {
            state.setMessage(slot + ": you already answered this challenge correctly -- wait for the next one.");
            return;
        }

        if (currentChallenge.isCorrect(guess)) {
            // correct answer
            if (slot == PlayerSlot.P1) {
                state.setPlayer1Score(state.getPlayer1Score() + 1);
                p1AnsweredCorrectly = true;
            } else {
                state.setPlayer2Score(state.getPlayer2Score() + 1);
                p2AnsweredCorrectly = true;
            }
            state.setMessage("[OK] " + slot + " guessed correctly: \"" + guess + "\"! +1 point.");

            // if both answered, move on
            if (p1AnsweredCorrectly && p2AnsweredCorrectly) {
                state.setMessage(state.getMessage() + "\nBoth players answered -- new challenge!");
                assignNewChallenge();
            }
        } else {
            state.setMessage("[X] " + slot + " guessed \"" + guess + "\" -- wrong! Keep trying.");
        }
    }

    /** Handles a USE <itemName> command from the given player. */
    private void handleItemUse(PlayerSlot slot, String itemKey) {
        switch (itemKey) {
            case "POTION" -> usePotion(slot);
            case "STOP" -> useStopwatch(slot);
            case "SWAP" -> useSwapCard(slot);
            case "HINT" -> useHint(slot);
            default -> state.setMessage(slot + ": unknown item \"" + itemKey
                    + "\". Valid items: POTION, STOP, SWAP, HINT.");
        }
    }

    private void usePotion(PlayerSlot slot) {
        boolean isP1 = (slot == PlayerSlot.P1);
        if (isP1 ? p1Potions <= 0 : p2Potions <= 0) {
            state.setMessage(slot + ": no Health Potions left!");
            return;
        }
        if (isP1) {
            p1Potions--;
            p1Hearts = Math.min(MAX_HEARTS, p1Hearts + 1);
        } else {
            p2Potions--;
            p2Hearts = Math.min(MAX_HEARTS, p2Hearts + 1);
        }

        int newHearts = isP1 ? p1Hearts : p2Hearts;
        state.setMessage(slot + " used a Health Potion! HP: " + newHearts);
    }

    private void useStopwatch(PlayerSlot slot) {
        boolean isP1 = (slot == PlayerSlot.P1);
        if (isP1 ? p1Stops <= 0 : p2Stops <= 0) {
            state.setMessage(slot + ": no Stopwatches left!");
            return;
        }
        if (isP1)
            p1Stops--;
        else
            p2Stops--;

        challengeTimer += STOPWATCH_BONUS;
        state.setMessage(slot + " used a Stopwatch! Timer extended by "
                + STOPWATCH_BONUS + " turns. Time left: " + challengeTimer);
    }

    private void useSwapCard(PlayerSlot slot) {
        boolean isP1 = (slot == PlayerSlot.P1);
        if (isP1 ? p1Swaps <= 0 : p2Swaps <= 0) {
            state.setMessage(slot + ": no Swap Cards left!");
            return;
        }
        if (isP1)
            p1Swaps--;
        else
            p2Swaps--;

        assignNewChallenge();
        state.setMessage(slot + " used a Swap Card! New challenge assigned.");
    }

    private void useHint(PlayerSlot slot) {
        boolean isP1 = (slot == PlayerSlot.P1);
        if (isP1 ? p1Hints <= 0 : p2Hints <= 0) {
            state.setMessage(slot + ": no Hints left!");
            return;
        }
        if (isP1)
            p1Hints--;
        else
            p2Hints--;

        sharedHint = currentChallenge.getHint();
        state.setMessage(slot + " used a Hint! First letter revealed: " + sharedHint);
    }

    /** Compares scores then hearts to determine the end-of-match result. */
    private GameResult resolveByScore() {
        int p1Score = state.getPlayer1Score();
        int p2Score = state.getPlayer2Score();

        state.setMessage(
                "Match over!\n"
                        + player1.getPlayerName() + ": " + p1Score + " correct, " + p1Hearts + " hearts\n"
                        + player2.getPlayerName() + ": " + p2Score + " correct, " + p2Hearts + " hearts");

        if (p1Score != p2Score) {
            return p1Score > p2Score ? GameResult.PLAYER1_WIN : GameResult.PLAYER2_WIN;
        }
        // tied on score -- compare hearts
        if (p1Hearts != p2Hearts) {
            return p1Hearts > p2Hearts ? GameResult.PLAYER1_WIN : GameResult.PLAYER2_WIN;
        }
        // both equal -- draw (spec requirement)
        return GameResult.DRAW;
    }

    /** Builds a compact status panel written to state.boardSnapshot. */
    private String buildSnapshot() {
        int timeLeft = MAX_TURNS - state.getTurnNumber();

        return "=== Word Bomb Duel ==="
                + "\nMatch time left : " + timeLeft + " turns"
                + "\nChallenge timer : " + challengeTimer + " turns"
                + "\n"
                + "\n" + player1.getPlayerName()
                + "  HP:" + p1Hearts
                + "  pts:" + state.getPlayer1Score()
                + "  [pot:" + p1Potions + " stop:" + p1Stops
                + " swap:" + p1Swaps + " hint:" + p1Hints + "]"
                + "\n" + player2.getPlayerName()
                + "  HP:" + p2Hearts
                + "  pts:" + state.getPlayer2Score()
                + "  [pot:" + p2Potions + " stop:" + p2Stops
                + " swap:" + p2Swaps + " hint:" + p2Hints + "]"
                + "\n"
                + "\nDefinition: " + currentChallenge.getDefinition()
                + (sharedHint != null ? "\nHint: " + sharedHint : "")
                + (p1AnsweredCorrectly ? "\n[OK] " + player1.getPlayerName() + " answered correctly" : "")
                + (p2AnsweredCorrectly ? "\n[OK] " + player2.getPlayerName() + " answered correctly" : "");
    }

    // ================================================================
    // Metadata
    // ================================================================

    @Override
    public String getAdventureName() {
        return "Word Bomb Duel";
    }

    @Override
    public String getDescription() {
        return "5-minute word-guessing duel -- type the word from its definition, "
                + "manage hearts, and use items to survive!";
    }
}