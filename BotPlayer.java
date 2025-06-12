import java.util.LinkedList;
import java.util.Queue;

/**
 * BotPlayer class represents the computer controlled player, it extends the Player class
 */
public class BotPlayer extends Player {
    private Queue<String> queuedMoves;  // queue of moves for the bot to execute based on its goal
    private String currentGoal;         // the bot's current goal (e.g "GOLD", "EXIT" or "PLAYER")
    private Integer requiredGold;       // quantity of gold required to win
    private int moveCount;              // count of moves made so far
    private boolean traceEnabled;       // flag to control trace logging

    /**
     * Constructor for BotPlayer class
     *
     * @param position initial position of the bot
     * @param tile     tile that represents the player on the map
     */
    BotPlayer(Position position, Tile tile) {
        // initialise bot
        super(position, tile);
        queuedMoves = new LinkedList<>();
        currentGoal = null;
        requiredGold = null;
        moveCount = 0;
        traceEnabled = false;
    }

    /**
     * Turn trace on/off
     *
     * @param traceEnabled boolean to indicate whether trace is enabled or disabled
     */
    public void setTraceEnabled(boolean traceEnabled) {
        this.traceEnabled = traceEnabled;
    }

    /**
     * Is trace enabled
     *
     * @return boolean to indicate whether trace is enabled or disabled
     */
    public boolean isTraceEnabled() {
        return traceEnabled;
    }

    /**
     * Determines command bot will issue on its turn
     *
     * @return command issued
     */
    public String issueCommand() {
        moveCount++;   // increment the move count for each command issued

        // first command is to call HELLO to find out gold required
        if (requiredGold == null) {
            return "HELLO";
        }

        // execute queued moves based on earlier lookup and analysis
        if (!queuedMoves.isEmpty()) {
            return queuedMoves.poll();
        }

        if (currentGoal != null) {
            // call PICKUP when no more moves and goal is GOLD
            if ("GOLD".equals(currentGoal)) {
                currentGoal = null;
                return "PICKUP";
            }

            // call QUIT when no more moves ang goal is EXIT
            if ("EXIT".equals(currentGoal)) {
                currentGoal = null;
                return "QUIT";
            }
        }

        // default is to call LOOK to analyse the surrounding tiles and create a new goal
        return "LOOK";
    }

    /**
     * Handle the response to the HELLO command,
     * determine the gold required to win
     *
     * @param response string returned by Hello command,
     *                 contains the gold required to win in the format:
     *                 Gold to win: x
     */
    @Override
    public void handleHello(String response) {
        // quantity of gold required is present after the ": " string
        requiredGold = Integer.valueOf(response.substring(response.indexOf(":") + 2));
        if (traceEnabled) {
            System.out.println("Bot requires " + requiredGold + " gold");
        }
    }

    /**
     * Handle the response to the LOOK command,
     * analyse the returned map to set the bot's next moves
     *
     * @param localMap map returned by LOOK command,
     *                 5 by 5 grid showing bot's surroundings with the bot at the centre
     */
    @Override
    public void handleLook(Tile[][] localMap) {
        queuedMoves.clear();
        int stepsToGold = 0;
        Position goldPosition = null;
        Position playerPosition = null;
        Position exitPosition = null;

        // scan localMap for gold, player and exit
        for (int i = 0; i < localMap.length; i++) {
            for (int j = 0; j < localMap[i].length; j++) {
                if (localMap[i][j] == Tile.GOLD) {
                    // focus on nearest gold
                    int steps = Math.abs(i - 2) + Math.abs(j - 2);
                    if (stepsToGold == 0 || steps < stepsToGold) {
                        stepsToGold = steps;
                        goldPosition = new Position(i, j);
                        if (traceEnabled) {
                            System.out.println("Bot sees Gold");
                        }
                    }
                } else if (localMap[i][j] == Tile.PLAYER) {     // detect human player
                    playerPosition = new Position(i, j);
                    if (traceEnabled) {
                        System.out.println("Bot sees Player");
                    }
                } else if (localMap[i][j] == Tile.EXIT) {     // detect the exit
                    exitPosition = new Position(i, j);
                    if (traceEnabled) {
                        System.out.println("Bot sees Exit");
                    }
                }
            }
        }

        Position destination = null;
        // determine the bot's next goal and destination 
        if (getGoldOwned() == requiredGold && exitPosition != null) {
            // move to exit if we have enough gold
            destination = exitPosition;
            currentGoal = "EXIT";
        } else if (playerPosition != null) {
            // else chase player if seen
            destination = playerPosition;
            currentGoal = "PLAYER";
        } else if (stepsToGold != 0) {
            // else move to gold if seen
            destination = goldPosition;
            currentGoal = "GOLD";
        } else {
            // else make random move
            queuedMoves.add(getRandomMove(localMap));
        }

        if (destination != null) {
            // determine moves needed to reach destination
            queuedMoves = findPath(localMap, destination);
        }
    }

    /**
     * Find the sequence of moves needed to reach a destination on the local map,
     * it may not always be possible to reach the destination in which case we try to get as close as
     * possible to the destination
     *
     * @param localMap    5x5 grid showing bot's surroundings with the bot at the centre
     * @param destination the target position
     * @return list of moves need to reach destination
     */
    private static LinkedList<String> findPath(Tile[][] localMap, Position destination) {
        LinkedList<String> moves = new LinkedList<>();
        // player is initially at centre of 5x5 local map ie position [2][2]
        final Position current = new Position(2, 2);
        String move;
        do {
            move = findNextMove(localMap, current, destination);
            if (move != null) {
                moves.add(move);
            }
        } while (move != null);
        // return list of moves required to reach the destination
        return moves;
    }

    /**
     * Find the next move that moves closer to the destination whilst avoiding walls
     *
     * @param localMap    5x5 grid showing bot's surroundings with the bot at the centre
     * @param current     the bot's current position
     * @param destination the target position
     * @return command for the next valid move
     */
    private static String findNextMove(Tile[][] localMap, Position current, Position destination) {
        if (current.getRow() > destination.getRow() && Map.isPlayerMoveValid(localMap, current, Direction.NORTH)) {
            current.decrementRow();
            return "MOVE N";
        } else if (current.getRow() < destination.getRow() && Map.isPlayerMoveValid(localMap, current, Direction.SOUTH)) {
            current.incrementRow();
            return "MOVE S";
        } else if (current.getColumn() > destination.getColumn() && Map.isPlayerMoveValid(localMap, current, Direction.WEST)) {
            current.decrementColumn();
            return "MOVE W";
        } else if (current.getColumn() < destination.getColumn() && Map.isPlayerMoveValid(localMap, current, Direction.EAST)) {
            current.incrementColumn();
            return "MOVE E";
        }
        return null;
    }

    /**
     * Get a random move that avoids walls
     *
     * @param localMap 5x5 grid showing bot's surroundings with the bot at the centre
     * @return command for a random move
     */
    public static String getRandomMove(Tile[][] localMap) {
        // player is initially at centre of 5x5 local map ie position [2][2]
        final Position current = new Position(2, 2);
        Direction direction;
        boolean isValid;
        // retry until we have a valid move (ie one that does not move into a wall)
        do {
            direction = Direction.getRandomDirection();
            isValid = Map.isPlayerMoveValid(localMap, current, direction);
        } while (!isValid);
        return "MOVE " + direction.getCharacter();
    }

    /**
     * Get total number of moves the bot has made
     *
     * @return number of moves made has made
     */
    public int getMoveCount() {
        return moveCount;
    }
}
