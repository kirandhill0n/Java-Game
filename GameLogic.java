import java.util.Optional;
import java.util.Scanner;

/**
 * GameLogic class contains main(), it creates the map and players and then controls the game flow
 */
public class GameLogic {
    /**
     * Main will start the game
     *
     * @param args command line arguments (none are required)
     */
    public static void main(String[] args) {
        try {
            // ask user to set game mode
            final GameMode gameMode = userSelectGameMode();

            // ask user if trace should be enabled
            final boolean traceEnabled = userSelectTraceEnabled();

            // ask user to load map
            final Map map = userSelectMap();

            // create players
            final Position playerPosition = map.getRandomStartPosition(Optional.empty());
            final HumanPlayer humanPlayer = new HumanPlayer(playerPosition, Tile.PLAYER);
            final Position botPosition = map.getRandomStartPosition(Optional.of(humanPlayer.getPosition()));
            final BotPlayer botPlayer = new BotPlayer(botPosition, Tile.BOT);
            botPlayer.setTraceEnabled(traceEnabled); // Set trace mode for the bot

            // show the full map and player positions if trace is enabled
            if (traceEnabled) {
                map.printFullMap(Optional.empty(), Optional.empty());
                System.out.println(humanPlayer);
                System.out.println(botPlayer);
            }

            boolean continueGame = true;
            do {
                // show the full map with player positions if trace is enabled
                if (traceEnabled) {
                    map.printFullMap(Optional.of(humanPlayer), Optional.of(botPlayer));
                }

                // in Bot Test mode only the bot moves
                if (gameMode != GameMode.BOT_TEST) {
                    // human player takes turn
                    System.out.println("Enter command:");
                    final Scanner scanner = new Scanner(System.in);
                    final String command = scanner.nextLine().toUpperCase();
                    continueGame = processCommand(command, map, humanPlayer, botPlayer);
                }

                // bot takes turn
                if (continueGame) {
                    final String botCommand = botPlayer.issueCommand();
                    System.out.println("Bots command: " + botCommand);
                    continueGame = processCommand(botCommand, map, botPlayer, humanPlayer);
                }

                // check if bot has caught player
                if (botPlayer.getPosition().equals(humanPlayer.getPosition())) {
                    continueGame = false;
                    System.out.println("Bot has caught player in " + botPlayer.getMoveCount() + " moves!");
                }
            } while (continueGame);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * User is prompted to select the GameMode:
     * Player and Bot (PB)
     * Bot Test (T) - in this mode only the bot moves, this is useful to test the bots ability
     *
     * @return GameMode selected by user
     */
    private static GameMode userSelectGameMode() {
        System.out.println("Select game mode from:\n Player and Bot (P)\n Bot test (T) \nType P or T then press ENTER");
        final Scanner scanner = new Scanner(System.in);
        final String traceAnswer = scanner.nextLine().toUpperCase();
        if (traceAnswer.equals("P")) {
            return GameMode.PLAYER_AND_BOT;
        } else if (traceAnswer.equals("T")) {
            return GameMode.BOT_TEST;
        }
        throw new IllegalArgumentException("Invalid game mode");
    }

    /**
     * User is prompted to select whether trace is enabled or not,
     * trace logging will show the full map at the start of each turn and log the operations of the bot
     *
     * @return flag indicating enabled or disabled
     */
    private static boolean userSelectTraceEnabled() {
        System.out.println("Do you want to enable trace ? " +
                "Trace will show the full map and all player moves for each turn." +
                "\nType Y or N then press ENTER");
        final Scanner scanner = new Scanner(System.in);
        final String traceAnswer = scanner.nextLine().toUpperCase();
        return traceAnswer.equalsIgnoreCase("Y");
    }

    /**
     * User is prompted whether they have a map to load, if not the default map will be loaded
     *
     * @return the loaded map
     * @throws Exception exception if the supplied map can not be found or is invalid
     */
    private static Map userSelectMap() throws Exception {
        System.out.println("Do you want to load a map file?\nType Y or N then press ENTER");
        final Scanner scanner = new Scanner(System.in);
        final String loadMapFileAnswer = scanner.nextLine();
        final Map map;
        if (loadMapFileAnswer.equalsIgnoreCase("Y")) {
            //load a user-specified map file
            System.out.println("Enter the full path to the map file: (eg C:\\tmp\\map.txt then press ENTER)");
            final String mapFilePath = scanner.nextLine();
            map = new Map(mapFilePath);
        } else {
            // load default map file
            System.out.println("Loading default map");
            map = new Map("example_map.txt");
        }
        return map;
    }

    /**
     * Process the commands issued by the players
     *
     * @param command       string for given command
     * @param map           full game map
     * @param callingPlayer player issuing command
     * @param otherPlayer   other player
     * @return flag indicating if game is to continue
     */
    private static boolean processCommand(String command, Map map, Player callingPlayer, Player otherPlayer) {
        boolean continueGame = true;
        switch (command) {
            case "HELLO":
                final String message = "Gold to win: " + map.getGoldRequired();
                callingPlayer.handleHello(message);
                break;
            case "GOLD":
                System.out.println("Gold owned: " + callingPlayer.getGoldOwned());
                break;
            case "LOOK":
                Tile[][] localMap = map.getLocalMap(callingPlayer, otherPlayer);
                callingPlayer.handleLook(localMap);
                break;
            case "MOVE N":
            case "MOVE S":
            case "MOVE E":
            case "MOVE W":
                Direction direction = Direction.get(command.charAt(command.length() - 1));
                processMove(map, callingPlayer, direction);
                break;
            case "QUIT":
                processQuit(map, callingPlayer);
                continueGame = false;
                break;
            case "PICKUP":
                processPickup(map, callingPlayer);
                break;
            default:
                System.out.println("Invalid command");
                break;
        }
        return continueGame;
    }

    /**
     * Process the PICKUP command to collect gold
     * If successful, print: 'Success. Gold owned: x' and remove the gold at that position
     * If unsuccessful, print: 'Fail. Gold owned: x'
     *
     * @param map    full game map
     * @param player player issuing command
     */
    private static void processPickup(Map map, Player player) {
        final Tile tile = map.getTile(player.getPosition());
        if (tile == Tile.GOLD) {
            if (isPrintToConsole(player)) {
                System.out.println("Success. Gold owned: " + player.incrementGoldOwned());
            }
            map.setTile(player.getPosition(), Tile.SPACE);    // replace the tile with a SPACE
        } else {
            // no gold to collect
            if (isPrintToConsole(player)) {
                System.out.println("Fail. Gold owned: " + player.getGoldOwned());
            }
        }
    }

    /**
     * Process the QUIT command to finish the game
     * If successful, print: 'WIN for x' where x is either P (Player) or B (Bot)
     * If unsuccessful, print: 'LOSE'
     *
     * @param map    full game map
     * @param player player issuing command
     */
    private static void processQuit(Map map, Player player) {
        final Tile tile = map.getTile(player.getPosition());
        // if player is at EXIT tile with enough gold player wins
        if (tile == Tile.EXIT && player.getGoldOwned() >= map.getGoldRequired()) {
            System.out.println("WIN for " + player.getTile());
        } else {
            System.out.println("LOSE");
        }
    }

    /**
     * Process the MOVE command
     *
     * @param map       full game map
     * @param player    player issuing command
     * @param direction direction of move (ie NORTH, SOUTH, EAST, WEST)
     */
    private static void processMove(Map map, Player player, Direction direction) {
        // get tile at next position
        final Position nextPosition = new Position(player.getPosition());
        final Tile tile = switch (direction) {
            case NORTH -> map.getTile(nextPosition.decrementRow());
            case SOUTH -> map.getTile(nextPosition.incrementRow());
            case EAST -> map.getTile(nextPosition.incrementColumn());
            case WEST -> map.getTile(nextPosition.decrementColumn());
            default -> throw new IllegalArgumentException("Invalid direction");
        };

        // validate tile at next position and update position if valid
        switch (tile) {
            case SPACE:
            case EXIT:
            case GOLD:
                player.getPosition().setPosition(nextPosition);
                if (isPrintToConsole(player)) {
                    System.out.println("Success");
                }
                break;
            case WALL:
            default:
                if (isPrintToConsole(player)) {
                    System.out.println("Fail");
                }
                break;
        }
    }

    /**
     * Should we print to the console for the given player
     *
     * @param player player
     * @return boolean indicates whether to print or not
     */
    private static boolean isPrintToConsole(Player player) {
        // always print for the human player but only for the bot player if trace is enabled
        return player instanceof HumanPlayer || player instanceof BotPlayer botPlayer && botPlayer.isTraceEnabled();
    }
}
