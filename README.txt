Dungeon of Doom

Running program
1.	Install Java 21
2.	Compile all the java files using: <java bin path>\javac *.java
3.	Start the game using: <java bin path>\java GameLogic

Using Git Codespaces
Update project settings so that JDK Runtime is JavaSE-21 and Compiler bytecode version is 21.

Game features
Game Mode: Choose from 1) Bot and Player or 2) Bot Test
    Bot Test is useful to observe the functionality of the bot player.
    The bot will chase the player if seen, otherwise it collect gold, otherwise it will move randomly.
Enable Trace: Turning on trace will display the full map and bot details during gameplay.
Custom Map: Load your own map by providing its file path when prompted, or use the default map.
Javadoc is available at: DungeonOfDoom/Javadoc/package-summary.html

OO Design
Game components are represented by separate classes where each class encapsulates its functionality.
Inheritance is used to represent a generic abstract Player which is subclassed for specialisation by HumanPlayer and BotPlayer.
Commands are processed in a polymorphic manner in GameLogic by interacting with Player objects, the specific Player subclass then handles the response. 
Concepts such as tile and direction are represented by Enums because they have distinct values.