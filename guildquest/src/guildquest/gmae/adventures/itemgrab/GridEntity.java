package guildquest.gmae.adventures.itemgrab;

/**
 * A single entity occupying a cell on the AdventureGrid.
 * Used for players and spawned items in Item Grab Duel.
 */
public class GridEntity {

    private int row;
    private int col;
    private final String label;

    public GridEntity(int row, int col, String label) {
        this.row   = row;
        this.col   = col;
        this.label = label;
    }

    // ---- accessors ----

    public int    getRow()   { return row; }
    public int    getCol()   { return col; }
    public String getLabel() { return label; }

    public void setRow(int row) { this.row = row; }
    public void setCol(int col) { this.col = col; }

    @Override
    public String toString() {
        return label + "@(" + row + "," + col + ")";
    }
}
