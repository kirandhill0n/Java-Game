import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Map class handles reading the map from a file, validating its structure, and providing methods for interacting with the map's data
 */
public class Map {

    private int rowSize;
    private int columnSize;
    // map is 2D array
    // row 0 column 0 is the top left corner of the map
    private Tile[][] map;
    private final String mapName;
    private final int goldRequired;

    /**
     * Map constructor will read map from file
     *
     * @param filename the path to the map file
     * @throws Exception exception if the file cannot be read or if the map format is invalid
     */
    public Map(String filename) throws Exception {
        final Path path = Paths.get(filename);
        List<String> lines;
        // catch exception if file cannot be read
        try {
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            throw new Exception("Unable to open file at path: " + filename);
        }
        final Iterator<String> lineItr = lines.iterator();
        // read name header
        final String nameLine = lineItr.next();
        // ensure that the line starts with "name" 
        if (!nameLine.startsWith("name")) {
            throw new Exception("first line of map file must start with 'name', found: " + nameLine);
        }
        mapName = nameLine.substring(5);

        // read win header
        final String winLine = lineItr.next();
        // ensure line starts with "win" 
        if (!winLine.startsWith("win")) {
            throw new Exception("second line of map file must start with 'win', found: " + winLine);
        }
        //parse the remaining text into an integer to store the require amount of gold to win
        goldRequired = Integer.parseInt(winLine.substring(4));

        // read map
        int row = 0;
        while (lineItr.hasNext()) {
            final String line = lineItr.next();
            final char[] charArray = line.toCharArray();

            if (map == null) {
                // if map has not been initialised, initialise internal map and dimension variables
                rowSize = lines.size() - 2;     // row size given by total lines minus 2 header lines
                columnSize = charArray.length;
                map = new Tile[rowSize][columnSize];
            }
            // ensure all rows have same column size
            if (charArray.length != columnSize) {
                throw new Exception("map is not rectangular");
            }
            // read each row of characters to an array of Tile
            // resulting array stored in the map array, row by row
            map[row++] = Tile.readRow(charArray);
        }

        // analyse map to ensure it is valid
        int totalGold = 0;
        int totalSpaces = 0;
        int totalExits = 0;

        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < columnSize; j++) {
                if (map[i][j] == Tile.GOLD) {
                    totalGold++;
                } else if (map[i][j] == Tile.SPACE) {
                    totalSpaces++;
                } else if (map[i][j] == Tile.EXIT) {
                    totalExits++;
                }
            }
        }

        // check if there is enough gold on the map
        if (totalGold < goldRequired) {
            throw new Exception("Invalid map: insufficient gold on the map to meet the win requirement");
        }
        // check the map has enough spaces
        if (totalSpaces < 2) {
            throw new Exception("Invalid map: not enough space tiles");
        }
        if (totalExits < 1) {
            throw new Exception("Invalid map: no exit tile found");
        }
    }

    /**
     * Get the quantity of gold required to win
     *
     * @return quantity of gold to win
     */
    public int getGoldRequired() {
        return goldRequired;
    }


    /**
     * Get the Tile at the specified position
     *
     * @param position position of tile
     * @return Tile at specified position
     */
    public Tile getTile(Position position) {
        // check supplied position is within bounds of map
        final int row = position.getRow();
        final int column = position.getColumn();
        if (row >= 0 && row < rowSize && column >= 0 && column < columnSize) {
            return map[row][column];
        }
        // return null if position outside map
        return null;
    }

    /**
     * Set the Tile at the specified position
     *
     * @param position position of tile to be updated
     * @param tile     new Tile value
     */
    public void setTile(Position position, Tile tile) {
        map[position.getRow()][position.getColumn()] = tile;
    }

    /**
     * Print the full map to the console, optionally showing players' positions
     *
     * @param player1 optional first player to display
     * @param player2 optional second player to display
     */
    public void printFullMap(Optional<Player> player1, Optional<Player> player2) {
        // print map name and gold requirement 
        System.out.println("name " + mapName);
        System.out.print("win " + goldRequired);
        for (int i = 0; i < rowSize; i++) {
            System.out.println();
            for (int j = 0; j < columnSize; j++) {
                // override tile with player symbol if at that position
                final Position position = new Position(i, j);
                if (player1.isPresent() && player1.get().getPosition().equals(position)) {
                    System.out.print(player1.get().getTile());
                } else if (player2.isPresent() && player2.get().getPosition().equals(position)) {
                    System.out.print(player2.get().getTile());
                } else {
                    System.out.print(map[i][j]); // otherwise print the tile's character 
                }
            }
        }

        System.out.println();
    }


    /**
     * Get local 5x5 map centered around the calling player
     *
     * @param callingPlayer the player requesting the local map
     * @param otherPlayer   the other player
     * @return 5x5 Tile array representing a local map
     */
    public Tile[][] getLocalMap(Player callingPlayer, Player otherPlayer) {
        Tile[][] localMap = new Tile[5][5];
        // populate grid with tiles, areas out of map populate with wall symbol
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                int rowInMap = callingPlayer.getPosition().getRow() - 2 + i;
                int columnInMap = callingPlayer.getPosition().getColumn() - 2 + j;
                // only lookup in map if we are in bounds of map dimensions
                if (rowInMap >= 0 && rowInMap < rowSize && columnInMap >= 0 && columnInMap < columnSize) {
                    // display other player if they occupy the position
                    if (otherPlayer.getPosition().equals(new Position(rowInMap, columnInMap))) {
                        localMap[i][j] = otherPlayer.getTile();
                    } else {
                        localMap[i][j] = map[rowInMap][columnInMap];
                    }
                } else {
                    // when out of bounds fill with wall symbol
                    localMap[i][j] = Tile.WALL;
                }
            }
        }
        // set centre of local map with supplied char (calling player's symbol)
        localMap[2][2] = callingPlayer.getTile();
        return localMap;
    }

    /**
     * Print the map to the console
     *
     * @param map 2d Tile array representing map to print
     */
    public static void printMap(Tile[][] map) {
        final int columns = map.length;
        final int rows = map[0].length;
        // print each row
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                System.out.print(map[i][j]);
            }
            System.out.println();
        }
    }

    /**
     * Generate random starting position on map
     *
     * @param existingPlayerPosition optional position of an existing player (to avoid collision at same position)
     * @return a valid starting position
     */
    public Position getRandomStartPosition(Optional<Position> existingPlayerPosition) {
        final Random random = new Random();
        boolean validTile = false;
        int row = -1;
        int column = -1;
        // loop until we find valid start tile
        while (!validTile) {
            // generate random row number (between 0 and rowSize-1)
            row = random.nextInt(rowSize);
            // generate random column number (between 0 and columnSize-1)
            column = random.nextInt(columnSize);
            // can not place new player at same position as existing player
            if (existingPlayerPosition.isPresent() && existingPlayerPosition.get().equals(new Position(row, column))) {
                continue;
            }
            // only EXIT or SPACE tiles allowed as start position
            final Tile tileAtStart = map[row][column];
            if (tileAtStart == Tile.EXIT || tileAtStart == Tile.SPACE) {
                validTile = true;
            }
        }
        return new Position(row, column);
    }

    /**
     * Check if a player move is valid, a valid move avoids moving into a wall
     *
     * @param localMap  5x5 Tile array representing a localMap
     * @param position  current position of player
     * @param direction proposed direction of movement
     * @return flag indicating whether move is valid
     */
    public static boolean isPlayerMoveValid(Tile[][] localMap, Position position, Direction direction) {
        final Tile nextTile = switch (direction) {
            case NORTH -> localMap[position.getRow() - 1][position.getColumn()];
            case EAST -> localMap[position.getRow()][position.getColumn() + 1];
            case SOUTH -> localMap[position.getRow() + 1][position.getColumn()];
            case WEST -> localMap[position.getRow()][position.getColumn() - 1];
        };
        return nextTile != null && nextTile != Tile.WALL;
    }
}
