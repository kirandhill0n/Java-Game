/**
 * HumanPlayer class represents a human controlled player, it extends the Player class
 */
public class HumanPlayer extends Player {
    /**
     * Constructor for HumanPlayer class
     *
     * @param startPosition initial position of player
     * @param tile          character that represents player on map
     */
    public HumanPlayer(Position startPosition, Tile tile) {
        // call the superclass Player constructor to initialize the player's position and tile
        super(startPosition, tile);
    }
}
