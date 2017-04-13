import java.util.*;

/**
 * Contains the main logic part of the game, as it processes. And some stuff.
 *
 * @author : The unnamed tutor.
 */
public class GameLogic{
	
	private Map map;
	private HashMap<Integer, Player> players;
	private Random random;
	private boolean active;
	private DoDServerController controller;
    private final static HashMap<String, String> PLAYER_EXIT_MESSAGES = new HashMap<String, String>() {
        private static final long serialVersionUID = -5824983597569224316L;

        {
        put("WON", "Congratulations!!! \n You have escaped the Dungeon of Doom!!!!!! \nThank you for playing!");
        put("LOST", "Gosh darn... you've been caught by a bot! Better luck next time :)");
        put("QUIT", "Thank you for playing Dungeon of Doom!!");
        put("SERVER DISCONNECT", "Sorry guys, the server disconnected! Maybe try again later?");
        put("CONNECTION FAILURE", null);
    }};
    private final static HashMap<String, String> PLAYER_EXIT_MESSAGES2 = new HashMap<String, String>() {
        private static final long serialVersionUID = 5882086249082661168L;

        {
        put("WON", " has escaped the dungeon!! Maybe you can join them...");
        put("LOST", " has been caught by a bot... Will you have the same fate?");
        put("QUIT", " has quit the game :(");
        put("CONNECTION FAILURE", " has lost connection.");
    }};
	
	public GameLogic(DoDServerController controller){
	    this.controller = controller;
		map = new Map();
		map.readMap("maps/example_map.txt");
		players = new HashMap<>();
		random = new Random();
		active = true;
	}
	
	public synchronized void addPlayer(Player newPlayer){
		int[] spawnLocation = getSpawnLocation();
		newPlayer.setLocation(spawnLocation[0], spawnLocation[1]);
		players.put(newPlayer.getPlayerId(), newPlayer);
		newPlayer.start();
		if (newPlayer.isHuman()) {
            writeToAllHumansExceptPlayer(newPlayer, "NEW PLAYER: " + newPlayer.getUsername());
        } else {
		    writeToAllHumansExceptPlayer(newPlayer, "NEW BOT ADDED!!");
        }
		updateServerMap();
	}

	public synchronized void removePlayer(int id, String removeReason) {

	    System.out.println("Server : Player removed (" + id + ")");
        Player playerToRemove = players.get(id);

        if (!removeReason.equals("SERVER DISCONNECT")) {
            String removePlayerMessage;
            if (playerToRemove.isHuman())
                removePlayerMessage = "PLAYER EXIT: " + playerToRemove.getUsername() + PLAYER_EXIT_MESSAGES2.get(removeReason);
            else
                removePlayerMessage = "A BOT HAS LEFT THE GAME...";

            //Luke is the best
            //so that removing outside of the iterator doesn't fuck shit up
            writeToAllHumansExceptPlayer(playerToRemove, removePlayerMessage);
            players.remove(id);
            updateServerMap();
        }
		playerToRemove.exit(PLAYER_EXIT_MESSAGES.get(removeReason));
	}

	public synchronized void endGame() {
        for(Integer key : players.keySet()){
            removePlayer(key, "SERVER DISCONNECT");
        }
        players = new HashMap<>();
        active = false;
    }

	public synchronized void playerLostConnection(int id) {
	    if (players.containsKey(id)) {
	        removePlayer(id, "CONNECTION FAILURE");
        }
    }
	
	private synchronized int[] getSpawnLocation(){
		int[] randomLocation = new int[2];
		int x = random.nextInt(map.getMapWidth());
		int y = random.nextInt(map.getMapHeight());
		
		if(map.getTile(x, y) == '#' || getPlayerOccupyingTile(x, y) != null){
			x = random.nextInt(map.getMapWidth());
			y = random.nextInt(map.getMapHeight());
			while(map.getTile(x, y) == '#'){
				x = random.nextInt(map.getMapWidth());
				y = random.nextInt(map.getMapHeight());
			}
		}
		
		randomLocation[0] = x;
		randomLocation[1] = y;
		return randomLocation;
	}

	/**
     * Processes the command. It should return a reply in form of a String, as the protocol dictates.
     * Otherwise it should return the string "Invalid".
     *
     */
    public synchronized String processCommand(String action, int player) {
    	if (!gameRunning()) {
    		return "Game has been won...";
    	}
    	else {
            Player dodPlayer = players.get(player);
    		if(action != null && dodPlayer != null){
		    	String [] command = action.trim().split(" ");
				String answer = "FAIL";
				
				switch (command[0].toUpperCase()) {
				case "HELLO":
					answer = hello(dodPlayer);
					break;
				case "MOVE":
					if (command.length == 2 ) {
						answer = move(dodPlayer,command[1].toUpperCase().charAt(0));
					}
					break;
				case "PICKUP":
					answer = pickup(dodPlayer);
					break;
				case "LOOK":
					answer = look(dodPlayer);
					break;
				case "WHISPER":
				    if (command.length >= 3) {
                        answer = whisper(dodPlayer, command[1], action.replace(command[0], "").replace(command[1], "").trim());
                    } else {
				        answer = "INVALID WHISPER";
                    }
                    break;
                case "SHOUT":
                    answer = shout(dodPlayer, action.replace(command[0], "").trim());
                    break;
                case "USERNAME":
                    answer = username(dodPlayer, action.replace(command[0], "").trim());
                    break;
                case "USERNAMES":
                    answer = usernames(dodPlayer);
                    break;
				case "QUIT":
					quitGame(dodPlayer);
				}
				return answer;
	    	}
	    	else {
	    		return "FAIL";
	    	}
    	}
    }

    /**
     * @return if the game is running.
     */
    public boolean gameRunning() {
        return active;
    }

    /**
     * @return : Returns back gold player requires to exit the Dungeon.
     */
    private synchronized String hello(Player player) {
        return "GOLD: " + (map.getGoldToWin() - player.getCollectedGold());
    }

    /**
     * Checks if movement is legal and updates player's location on the map.
     *
     * @param direction : The direction of the movement.
     * @param player : The player who is moving
     * @return : Protocol if success or not.
     */
    private synchronized String move(Player player, char direction) {
    	int newX = player.getXCoordinate();
    	int newY = player.getYCoordinate();
		switch (direction){
		case 'N':
			newY -=1;
			break;
		case 'E':
			newX +=1;
			break;
		case 'S':
			newY +=1;
			break;
		case 'W':
			newX -=1;
			break;
		default:
			break;
		}
		// check if the player can move to that tile on the map
		Player playerOnTile = getPlayerOccupyingTile(newX,newY);
		if(playerOnTile != null) {
            if (player.isHuman() && playerOnTile.isHuman()) {
                // both human
                return "FAIL";
            } else if (player.isHuman() && !playerOnTile.isHuman()) {
                removePlayer(player.getPlayerId(), "LOST");
                return "GAME LOST";
            } else if (!player.isHuman() && playerOnTile.isHuman()) {
                removePlayer(playerOnTile.getPlayerId(), "LOST");
            } else {
                return "FAIL";
            }
        } else if(map.getTile(newX, newY) == '#') {
            return "FAIL";
        }
		player.setXCoordinate(newX);
		player.setYCoordinate(newY);
		if (checkWin(player)){
			//active = false;
            removePlayer(player.getPlayerId(), "WON");
			//return "Congratulations!!! \n You have escaped the Dungeon of Doom!!!!!! \n" + "Thank you for playing!";
            return "am i even a thing?";
		}
		updateServerMap();
		return "SUCCESS";
    }
    
    // checks to see if another player is in the location a player wants to move to
    private synchronized Player getPlayerOccupyingTile(int newX, int newY){
    	Collection<Player> list = players.values();
    	Iterator<Player> listIterator = list.iterator();
    	while(listIterator.hasNext()){
    	    Player player = listIterator.next();
    		if(player.occupiesSameTile(newX, newY)){
    			return player;
    		}
    	}
    	return null;
    }

    /**
     * Processes the player's pickup command, updating the map and the player's gold amount.
     *
     * @return If the player successfully picked-up gold or not.
     */
    private synchronized String pickup(Player player) {
        if (map.getTile(player.getXCoordinate(), player.getYCoordinate()) == 'G') {
            player.incrementCollectedGold();
            map.replaceTile(player.getXCoordinate(), player.getYCoordinate(), '.');
            return "GOLD COINS: " + player.getCollectedGold();
        }

        return "FAIL" + "\n" + "There is nothing to pick up...";
    }

    /**
     * Converts the map from a 2D char array to a single string.
     *
     * @return : A String representation of the game map.
     */
    private synchronized String look(Player player) {

        int distance = (map.LOOK_RADIUS-1)/2;
    	// get look window for current player
    	char[][] l = map.look(player.getXCoordinate(), player.getYCoordinate());
    	// add current player's icon to look window
    	l[distance][distance] = player.getIcon();
    	// is any opponent visible? if they are then add them to the look window
    	char[][] look = getVisibleOpponents(l, player);
    	// return look window as a String for printing
    	String lookWindow = "";
    	for(int i=0; i<look.length; i++){
    		for(int j=0; j<look[i].length; j++){
    			lookWindow += look[j][i];
    		}
    		lookWindow += "\n";
    	}
        return lookWindow;
    }
    
    // are there other players visible to the player calling look? if there are then add them to their look window
    private synchronized char[][] getVisibleOpponents(char[][] look, Player player){
        int distance = (map.LOOK_RADIUS-1)/2;
    	Collection<Player> list = players.values();
    	Iterator<Player> listIterator = list.iterator();
    	while(listIterator.hasNext()){
            Player opp = listIterator.next();
    		int xDistance =  player.getXCoordinate() - opp.getXCoordinate();
        	int yDistance = player.getYCoordinate() - opp.getYCoordinate();
        	if(xDistance <= distance && xDistance >= -distance && yDistance <= distance && yDistance >= -distance){
        		look[distance-xDistance][distance-yDistance] = opp.getIcon();
        	}
    	}
    	return look;
    }

    /**
     * Sends a private message to another player.
     *
     * @return : A String containing the message.
     */
    private synchronized String whisper(Player fromPlayer, String toPlayerUsername, String message) {
        String response;
        Player toPlayer = getHumanPlayerFromUsername(toPlayerUsername);
        if (toPlayer == null) {
            response = "INVALID PLAYER: " + toPlayerUsername;
        } else {
            toPlayer.writeToClient(fromPlayer.getUsername() + " (TO YOU): " + message);
            response = "YOU (TO " + toPlayer.getUsername() + "): " + message;
        }

        return response;
    }

    /**
     * Broadcasts a message to the game chat.
     *
     * @return : A String containing the message.
     */
    private synchronized String shout(Player fromPlayer, String message) {
        String messageToPlayers = fromPlayer.getUsername() + " (TO ALL): " + message;
        writeToAllHumansExceptPlayer(fromPlayer, messageToPlayers);
        return "YOU (TO ALL): " + message;
    }

    private synchronized String username(Player player, String username) {
        String response;
        if (username == null || username.equals("")) {
            response = "USERNAME: " + player.getUsername();
        } else if (isValidUsername(username)) {
            if (player.getUsername().equals(username)) {
                response = "USERNAME UNCHANGED: " + username;
            } else if (username.length() > 14) {
                return "MAXIMUM USERNAME LENGTH 14";
            } else if (usernameExists(username)) {
                response = "TAKEN USERNAME: " + username;
            } else {
                writeToAllHumansExceptPlayer(player, "USERNAME: " + player.getUsername() + " UPDATED TO: " + username);
                player.setUsername(username);
                response = "USERNAME CHANGED: " + username;
            }
        } else {
            response = "INVALID USERNAME: " + username;
        }
        return response;
    }

    private synchronized boolean usernameExists(String username) {
        Collection<Player> list = players.values();
        Iterator<Player> listIterator = list.iterator();
        while (listIterator.hasNext()) {
            Player player = listIterator.next();
            if (player.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    private synchronized boolean isValidUsername(String username) {
        return username.matches("^[a-zA-Z]+[a-zA-Z0-9]*$");
    }

    private synchronized String usernames(Player player) {
        Collection<Player> list = players.values();
        Iterator<Player> listIterator = list.iterator();

        StringBuilder response = new StringBuilder();
        int numberOfHumanPlayers = 0;
        while(listIterator.hasNext()) {
            Player otherPlayer = listIterator.next();
            if (player != otherPlayer && otherPlayer.isHuman()) {
                numberOfHumanPlayers++;
                response.append(otherPlayer.getPlayerId() + "-" + otherPlayer.getUsername() + "\t");
            }
        }
        String s = numberOfHumanPlayers == 1 ? "" : "S";
        response.insert(0, numberOfHumanPlayers + " OTHER PLAYER" + s + " ACTIVE:\t");
        return response.toString();
    }

    private synchronized Player getHumanPlayerFromUsername(String username) {
        if (isValidUsername(username) || username.matches("PLAYER_[0-9]+")) {
            Collection<Player> list = players.values();
            Iterator<Player> listIterator = list.iterator();
            while(listIterator.hasNext()) {
                Player currentPlayer = listIterator.next();
                if (currentPlayer.getUsername().equals(username) && currentPlayer.isHuman())
                    return currentPlayer;
            }
        }
        return null;
    }

    private synchronized void writeToAllHumansExceptPlayer(Player player, String message) {
        Collection<Player> list = players.values();
        Iterator<Player> listIterator = list.iterator();
        while (listIterator.hasNext()) {
            Player iteratedPlayer = listIterator.next();
            if (player != iteratedPlayer && iteratedPlayer.isHuman())
                iteratedPlayer.writeToClient(message);
        }
    };

    /**
	 * checks if the player collected all GOLD and is on the exit tile
	 * @return True if all conditions are met, false otherwise
	 */
	private synchronized boolean checkWin(Player player) {
		if (player.getCollectedGold() >= map.getGoldToWin() && 
			map.getTile(player.getXCoordinate(), player.getYCoordinate()) == 'E') {
			return true;
		}
		return false;
	}

	/**
	 * Quits the game when called i.e. removes the player from the game.
	 */
	private synchronized void quitGame(Player player) {
		removePlayer(player.getPlayerId(), "QUIT");
	}

	private synchronized void dumpMap() {
	    char[][] tempMap = getPopulatedMap();
	    for (int currentY = 0; currentY < map.getMapHeight(); currentY++) {
	        for (int currentX = 0; currentX < map.getMapWidth(); currentX++) {
	            System.out.print(tempMap[currentY][currentX]);
            }
            if (currentY != map.getMapHeight() + 1) System.out.println();
        }
    }


    public synchronized char[][] getPopulatedMap() {

        char[][] tempMap = map.getMap();
        Collection<Player> list = players.values();
        Iterator<Player> listIterator = list.iterator();
        while (listIterator.hasNext()) {
            Player player = listIterator.next();
            tempMap[player.getYCoordinate()][player.getXCoordinate()] = player.getIcon();
        }
        return tempMap;
    }

    private synchronized void updateServerMap() {
	    controller.updateMapGrid();
    }
}
