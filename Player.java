import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;

public class Player extends Thread {

    private Socket socket;
	private BufferedReader in;
	private BufferedWriter out;
	private GameLogic game;
	private boolean isHuman;
	private String username;
	private int id;
    private int collectedGold;
    private int x;
    private int y;
    private boolean accpetingInput;

	public Player(Socket socket, GameLogic game, int id){
	    this.socket = socket;
		this.id = id;
		this.username = "PLAYER_" + id;
		this.game = game;
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            determineTypeOfPlayer();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Player Thread : New Player Thread Created (" + id + ")");
	}

	private void determineTypeOfPlayer() throws IOException {
        String typeOfPlayer = in.readLine();
        switch(typeOfPlayer) {
            case "human":
                isHuman = true;
                break;
            case "bot":
                isHuman = false;
                break;
            default:
                exit("INVALID CONNECTION");
        }
    }

	public void run() {
        System.out.println("Player Thread Running : (" + id + ")");
        writeToClient("Welcome to DOD");
        accpetingInput = true;
        String input = readInputFromClient();
        String result;
        while (input != null) {
            result = game.processCommand(input, id);
            if (result.equals("GAME LOST"))
                break;
            writeToClient(result);
            input = readInputFromClient();
        }
        System.out.println("Player Thread Stopped : (" + id + ")");
	}

	private String readInputFromClient() {
	    String input = null;
	    try {
	        input = in.readLine();
        } catch (SocketException e) {
            game.playerLostConnection(id);
        } catch (IOException e) {
            e.printStackTrace();
        }
	    return input;
    }

	public void writeToClient(String message) {
	    try {
	        if (!socket.isClosed()) {
                out.write(message);
                out.newLine();
                out.flush();
            }
        } catch (SocketException e) {
	        //System.out.println("Player unexpectedly disconnected : (" + id + ")");
        } catch (IOException e) {
	        e.printStackTrace();
        }
    }

    public void exit(String message) {
        // todo: check
        writeToClient("bye bye\n" + message);
	    //if (message != null)
	    //    writeToClient(message);
	    try {
            socket.close();
        } catch(IOException e) {
	        e.printStackTrace();
        }
    }

    public void setLocation(int x, int y) {
	    this.x = x;
	    this.y = y;
	    collectedGold = 0;
    }

    public String getUsername() {
	    return username;
    }

    public void setUsername(String newUsername) {
	    username = newUsername;
    }

    public int getPlayerId(){
        return id;
    }

    public char getIcon(){
        //return Character.forDigit(id,10);
        return isHuman ? 'H' : 'B';
    }

    public int getCollectedGold(){
        return collectedGold;
    }

    public void incrementCollectedGold(){
        collectedGold++;
    }

    public boolean isHuman() {
        return isHuman;
    }

    public int getXCoordinate(){
        return x;
    }

    public void setXCoordinate(int newX){
        x = newX;
    }

    public int getYCoordinate(){
        return y;
    }

    public void setYCoordinate(int newY){
        y = newY;
    }

    public boolean occupiesSameTile(int otherPlayerX, int otherPlayerY){
        if(x == otherPlayerX && y == otherPlayerY){
            return true;
        }
        return false;
    }

}
