/**
 * Position class represents a location using row and column coordinates, it also provides methods for changing and comparing position
 */
public class Position {
    private int row;        // the row number of the position
    private int column;     // the column number of the position

    /**
     * Constructor for Position, initialise with specified row and column number
     *
     * @param row    row number (zero based)
     * @param column column number (zero based)
     */
    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    /**
     * Copy constructor for Position, create a new Position object from an existing object
     *
     * @param position Position os object to copy
     */
    public Position(Position position) {
        this.row = position.getRow();
        this.column = position.getColumn();
    }

    /**
     * Update Position with specified row and column values
     *
     * @param position new Position
     */
    //
    public void setPosition(Position position) {
        this.row = position.getRow();
        this.column = position.getColumn();
    }

    /**
     * Get row value of Position
     *
     * @return row value
     */
    public int getRow() {
        return row;
    }

    /**
     * Get column value of Position
     *
     * @return column value
     */
    public int getColumn() {
        return column;
    }

    /**
     * Increment row value of Position
     *
     * @return new Position after increment of row
     */
    public Position incrementRow() {
        this.row++;
        return this;
    }

    /**
     * Decrement row value of Position
     *
     * @return new Position after decrement of row
     */
    public Position decrementRow() {
        this.row--;
        return this;
    }

    /**
     * Increment column value of Position
     *
     * @return new Position after increment of column
     */
    public Position incrementColumn() {
        this.column++;
        return this;
    }

    /**
     * Decrement column value of Position
     *
     * @return new Position after decrement of column
     */
    public Position decrementColumn() {
        this.column--;
        return this;
    }

    /**
     * String representation of Position
     *
     * @return string representing Position
     */
    @Override
    public String toString() {
        return "row=" + row + " column=" + column;
    }

    /**
     * Equals check for Position, overriding default implementation to allow test for object equality
     *
     * @param obj other object to compare with
     * @return boolean for is equal or not
     */
    //
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Position other) {   // check object is an instance of Position
            // test for equality by comparing the row and column values of both positions, return true if both match otherwise false
            return other.getRow() == this.row && other.getColumn() == this.column;
        }
        return false;
    }

    /**
     * hashCode for Position, overriding default implementation because we have overridden equals()
     *
     * @return hashcode value for object
     */
    //
    @Override
    public int hashCode() {
        // generate a hash code based on row and column values
        return row + column;
    }
}
