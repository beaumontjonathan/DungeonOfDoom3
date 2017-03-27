import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.Socket;

/**
 * Runs the game with a human player and contains code needed to read inputs.
 *
 * @author : The unnamed tutor.
 */
public class HumanPlayer implements Runnable {

    private Socket server;

	public HumanPlayer(String hostName, int portNumber) {
		try {
			this.server = new Socket(hostName, portNumber);
		} catch (ConnectException e) {
		    System.out.println("UNABLE TO CONNECT TO SERVER");
		    System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {

        new Thread(new HumanPlayerSend(server)).start();
        //new Thread(new HumanPlayerReceive(server)).start();

	}

}