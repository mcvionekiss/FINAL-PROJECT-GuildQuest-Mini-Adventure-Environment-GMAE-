package guildquest.gmae.adventures.itemgrab;

import guildquest.gmae.engine.AbstractMiniAdventure;
import guildquest.gmae.enums.GameResult;
import guildquest.gmae.enums.InputType;
import guildquest.gmae.enums.PlayerSlot;

/**
 * Item Grab Duel — a two-player mini-adventure played on a 5×5 grid.
 *
 * <p>Rules:
 * <ul>
 *   <li>Each turn a player moves one step (N / S / E / W).</li>
 *   <li>Stepping onto a cell that contains an item token collects it.</li>
 *   <li>New item tokens spawn on the grid every {@value #ITEM_SPAWN_INTERVAL} turns.</li>
 *   <li>After {@value #MAX_TURNS} turns (≈ 3 minutes at 1 turn/sec) the player
 *       with the most collected items wins; equal counts result in a draw.</li>
 * </ul>
 *
 * <p>Reuses {@link AdventureGrid} for all grid / item logic, and reads the
 * current realm and world-clock from the injected {@code Realm} / {@code WorldClock}
 * (provided by the engine — not modified here).
 */
public class ItemGrabDuel extends AbstractMiniAdventure {

    // ---- timing constants (1 turn ≈ 1 second) ----
    /** Total duration of the match in turns (3 minutes). */
    private static final int MAX_TURNS            = 180;
    /** How many turns between automatic item spawns (10 seconds). */
    private static final int ITEM_SPAWN_INTERVAL  = 10;
    /** Number of item tokens spawned at the start. */
    private static final int INITIAL_ITEM_COUNT   = 3;
    /** Grid dimensions. */
    private static final int GRID_SIZE            = 5;

    // ---- adventure state ----
    private AdventureGrid grid;
    private GridEntity    p1Entity;
    private GridEntity    p2Entity;
    private int           itemSpawnCounter;
    private int           nextItemId;

    // ================================================================
    //  Lifecycle
    // ================================================================

    @Override
    protected void onStart() {
        grid = new AdventureGrid(GRID_SIZE, GRID_SIZE);

        // players start at opposite corners
        p1Entity = new GridEntity(0, 0, "P1");
        p2Entity = new GridEntity(GRID_SIZE - 1, GRID_SIZE - 1, "P2");

        state.setPlayer1Score(0);
        state.setPlayer2Score(0);
        itemSpawnCounter = 0;
        nextItemId       = 0;

        // initial item tokens
        for (int i = 0; i < INITIAL_ITEM_COUNT; i++) {
            spawnNewItem();
        }

        String realmName  = (realm == null) ? "Unknown Realm" : realm.getName();
        String worldTime  = (clock == null) ? "Unknown Time"  : clock.now().toString();

        state.setMessage(
                "Item Grab Duel started in " + realmName + " at " + worldTime
                + "\nMove with N / S / E / W to collect items!"
                + " First to collect the most items in " + MAX_TURNS + " turns wins."
        );
        state.setBoardSnapshot(buildSnapshot());
    }

    @Override
    protected void onInput(PlayerSlot slot, InputType inputType, String payload) {
        if (payload == null || payload.isBlank()) {
            state.setMessage(slot + ": empty input -- move with N, S, E, or W.");
            return;
        }

        GridEntity mover     = (slot == PlayerSlot.P1) ? p1Entity : p2Entity;
        String     direction = payload.trim().toUpperCase();

        boolean moved = grid.move(mover, direction);
        if (!moved) {
            state.setMessage(slot + ": cannot move " + direction
                    + " — boundary or invalid direction. Use N/S/E/W.");
            return;
        }

        // collect any item tokens at the new cell
        java.util.List<GridEntity> collected = grid.collectItemsAt(mover.getRow(), mover.getCol());
        int gained    = collected.size();

        if (slot == PlayerSlot.P1) {
            state.setPlayer1Score(state.getPlayer1Score() + gained);
        } else {
            state.setPlayer2Score(state.getPlayer2Score() + gained);
        }

        if (gained > 0) {
            state.setMessage(slot + " moved " + direction
                    + " and collected " + gained + " item(s)! ★");
        } else {
            state.setMessage(slot + " moved " + direction + ".");
        }
    }

    @Override
    protected void onTick() {
        // spawn new items on interval
        itemSpawnCounter++;
        if (itemSpawnCounter >= ITEM_SPAWN_INTERVAL) {
            spawnNewItem();
            itemSpawnCounter = 0;
        }

        state.setBoardSnapshot(buildSnapshot());
    }

    @Override
    protected GameResult evaluateWinCondition() {
        if (state.getTurnNumber() >= MAX_TURNS) {
            int p1 = state.getPlayer1Score();
            int p2 = state.getPlayer2Score();

            state.setMessage(
                    "Time's up! Final scores -- "
                    + player1.getPlayerName() + ": " + p1 + " item(s)"
                    + " | " + player2.getPlayerName() + ": " + p2 + " item(s)."
            );

            if (p1 > p2) return GameResult.PLAYER1_WIN;
            if (p2 > p1) return GameResult.PLAYER2_WIN;
            return GameResult.DRAW;
        }
        return GameResult.ONGOING;
    }

    // ================================================================
    //  Helpers
    // ================================================================

    private void spawnNewItem() {
        nextItemId++;
        GridEntity item = new GridEntity(0, 0, "I" + nextItemId);
        grid.spawnItem(item);
    }

    private String buildSnapshot() {
        int    turnsLeft  = MAX_TURNS - state.getTurnNumber();
        int    onField    = grid.getSpawnedItems().size();

        return "Turn " + state.getTurnNumber() + "/" + MAX_TURNS
                + "  |  Time left: " + turnsLeft + "s"
                + "  |  Items on field: " + onField
                + "\nP1 [" + player1.getPlayerName() + "]: "
                        + state.getPlayer1Score() + " item(s) collected"
                + "\nP2 [" + player2.getPlayerName() + "]: "
                        + state.getPlayer2Score() + " item(s) collected"
                + "\n\n" + grid.render(p1Entity, p2Entity)
                + "\n\nLegend: 1=P1  2=P2  *=item  .=empty  B=both";
    }

    // ================================================================
    //  Metadata
    // ================================================================

    @Override
    public String getAdventureName() {
        return "Item Grab Duel";
    }

    @Override
    public String getDescription() {
        return "Race across a 5x5 grid (N/S/E/W) collecting items -- "
                + "most items after 3 minutes wins!";
    }
}
