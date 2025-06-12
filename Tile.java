import java.util.HashMap;
import java.util.Map;


/**
 * Tile enum represents the types of tiles that can appear on the map
 */
public enum Tile {
    EXIT('E'),
    GOLD('G'),
    SPACE('.'),
    WALL('#'),
    PLAYER('P', true),
    BOT('B', true);

    private final Character character;
    private final boolean isPlayer;       // indicates whether tile is for a player (either hunan or bot)
    private final static Map<Character, Tile> mapping;  // map characters to their corresponding Tile

    // initialize the mapping of characters to Tile values
    static {
        mapping = new HashMap<>();
        for (Tile tile : Tile.values()) {
            // add each Tile to the mapping using its character as the key
            if (mapping.put(tile.getCharacter(), tile) != null) {
                // if duplicate character is detected, throw an exception
                throw new IllegalStateException("Duplicate character can not be used in Tile");
            }
        }
    }

    /**
     * Constructor for Tile
     *
     * @param character char for Tile
     * @param isPlayer  is this Tile representing a player
     */
    Tile(Character character, boolean isPlayer) {
        this.character = character;
        this.isPlayer = isPlayer;
    }

    /**
     * Constructor for Tile
     *
     * @param character char for Tile
     */
    // assign the character to the Tile enum
    Tile(char character) {
        this(character, false);
    }

    /**
     * Read array of characters into an array of Tile
     *
     * @param characters char array
     * @return Tile array
     */
    public static Tile[] readRow(char[] characters) {
        final Tile[] tiles = new Tile[characters.length]; // create array to hold Tile enums
        for (int i = 0; i < characters.length; i++) {
            final Tile tile = mapping.get(characters[i]); // look up the corresponding Tile for the character
            // initial map can not provide player positions
            if (tile == null || tile.isPlayer) {
                // throw exception if any character is not recognized as a valid Tile
                throw new IllegalArgumentException("Unexpected character in map: " + characters[i]);
            }
            tiles[i] = tile; // add corresponding Tile to the array
        }
        return tiles; // return array of Tile enums
    }

    /**
     * Get Character associating with Tile
     *
     * @return character for Tile
     */
    // returns character associated with the Tile
    public Character getCharacter() {
        return character;
    }

    /**
     * String representation of Tile
     *
     * @return string representation of Tile
     */
    @Override
    public String toString() {
        return character.toString();
    }
}