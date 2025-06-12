/**
 * Player class represents a generic player, the class is abstract, to be extended by specific player types (eg human player, bot player)
 */
public abstract class Player {
    private final Position position;    // current position on the map
    private final Tile tile;            // tile representing player on the map
    private int goldOwned;              // quantity of gold owned

    /**
     * Constructor for Player class
     *
     * @param position players initial position on map
     * @param tile     tile that represents the player on the map
     */
    Player(Position position, Tile tile) {
        // initialise player
        this.position = position;
        this.tile = tile;
        this.goldOwned = 0;     // player starts no gold
    }

    /**
     * Get current position of player on map
     *
     * @return current position of player on map
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Get the quantity of gold the player owns
     *
     * @return quantity of gold the player owns
     */
    public int getGoldOwned() {
        return goldOwned;
    }

    /**
     * Increment by 1 the quantity of gold the player owns
     *
     * @return new quantity of gold the player owns
     */
    public int incrementGoldOwned() {
        return ++goldOwned;
    }

    /**
     * Get the Tile for the player
     *
     * @return Tile for the player
     */
    public Tile getTile() {
        return tile;
    }

    /**
     * Handle response from HELLO command, by default print response message to console
     *
     * @param message string returned by Hello command
     */
    void handleHello(String message) {
        System.out.println(message);
    }

    /**
     * Handle response from LOOK command, by default print response map to console
     *
     * @param localMap map returned by LOOK command
     */
    void handleLook(Tile[][] localMap) {
        Map.printMap(localMap);
    }

    /**
     * Provide string representation of the player including tile, position and gold owned
     *
     * @return string representation of players state
     */
    @Override
    public String toString() {
        return "Player " + tile + " at " + position + " owns " + goldOwned + " gold";
    }
}
