import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Direction class for the directions a player can move
 */
public enum Direction {
    NORTH('N'),
    EAST('E'),
    SOUTH('S'),
    WEST('W');

    private final static Map<Character, Direction> mapping; // link characters to Direction

    // initialize the mapping of characters to Direction values
    static {
        mapping = new HashMap<>();
        for (Direction direction : Direction.values()) {
            // add each Direction to the mapping using its character as the key
            if (mapping.put(direction.getCharacter(), direction) != null) {
                // if duplicate character is detected, throw an exception
                throw new IllegalStateException("Duplicate character can not be used in Direction");
            }
        }
    }

    private final char character;

    /**
     * Constructor for Direction class
     *
     * @param character representing direction eg N, E, S, W
     */
    Direction(char character) {
        this.character = character;
    }

    public static Direction getRandomDirection() {
        final Random random = new Random();
        return Arrays.asList(values()).get(random.nextInt(values().length));
    }

    /**
     * Get character representation of Direction
     *
     * @return char representing Direction eg N, E, S, W
     */
    public char getCharacter() {
        return character;
    }

    /**
     * Get Direction for given character direction
     *
     * @param direction char representing direction eg N, E, S, W
     * @return Direction for given character
     */
    public static Direction get(char direction) {
        final Direction answer = mapping.get(direction);
        if (answer == null) {
            throw new IllegalArgumentException("No such direction: " + direction);
        }
        return answer;
    }
}
