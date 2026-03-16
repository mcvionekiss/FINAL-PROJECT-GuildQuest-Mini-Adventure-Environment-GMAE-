package guildquest.gmae.adventures.itemgrab;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A 2-D grid used by ItemGrabDuel.
 * Tracks the positions of two players and any spawned item tokens.
 */
public class AdventureGrid {

    private final int rows;
    private final int cols;
    private final List<GridEntity> spawnedItems;
    private final Random rng;

    public AdventureGrid(int rows, int cols) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Grid dimensions must be positive.");
        }
        this.rows = rows;
        this.cols = cols;
        this.spawnedItems = new ArrayList<>();
        this.rng = new Random();
    }

    // ---- movement ----

    /**
     * Moves {@code entity} one cell in the given cardinal direction.
     *
     * @param entity    the entity to move
     * @param direction "N", "S", "E", or "W" (case-insensitive)
     * @return {@code true} if the move succeeded; {@code false} if it would
     *         leave the grid or the direction string is unrecognised
     */
    public boolean move(GridEntity entity, String direction) {
        int newRow = entity.getRow();
        int newCol = entity.getCol();

        switch (direction.toUpperCase()) {
            case "N" -> newRow--;
            case "S" -> newRow++;
            case "E" -> newCol++;
            case "W" -> newCol--;
            default -> {
                return false;
            }
        }

        if (newRow < 0 || newRow >= rows || newCol < 0 || newCol >= cols) {
            return false; // boundary hit
        }

        entity.setRow(newRow);
        entity.setCol(newCol);
        return true;
    }

    // ---- item management ----

    /**
     * Places a new item token at a random cell on the grid.
     */
    public void spawnItem(GridEntity item) {
        item.setRow(rng.nextInt(rows));
        item.setCol(rng.nextInt(cols));
        spawnedItems.add(item);
    }

    /**
     * Removes and returns all item tokens whose position matches
     * {@code (row, col)}. Call this when a player steps onto a cell.
     */
    public List<GridEntity> collectItemsAt(int row, int col) {
        List<GridEntity> collected = new ArrayList<>();
        spawnedItems.removeIf(item -> {
            if (item.getRow() == row && item.getCol() == col) {
                collected.add(item);
                return true;
            }
            return false;
        });
        return collected;
    }

    /** Returns a read-only view of items currently on the grid. */
    public List<GridEntity> getSpawnedItems() {
        return List.copyOf(spawnedItems);
    }

    // ---- rendering ----

    /**
     * Renders the grid as a multi-line ASCII string.
     * Legend: [1] = P1, [2] = P2, [*] = item, [.] = empty.
     * If both players share a cell the cell shows [B].
     */
    public String render(GridEntity p1, GridEntity p2) {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < rows; r++) {
            sb.append("|");
            final int row = r; // effectively-final copy for lambda capture
            for (int c = 0; c < cols; c++) {
                final int col = c; // effectively-final copy for lambda capture

                boolean hasP1 = p1.getRow() == row && p1.getCol() == col;
                boolean hasP2 = p2.getRow() == row && p2.getCol() == col;
                boolean hasItem = spawnedItems.stream()
                        .anyMatch(it -> it.getRow() == row && it.getCol() == col);

                if (hasP1 && hasP2)
                    sb.append("B|");
                else if (hasP1)
                    sb.append("1|");
                else if (hasP2)
                    sb.append("2|");
                else if (hasItem)
                    sb.append("*|");
                else
                    sb.append(".|");
            }
            sb.append("\n");
        }
        return sb.toString().trim();
    }

    // ---- size accessors ----

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }
}
