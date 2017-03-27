import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class HumanPlayerSend implements Runnable {

    private Socket server;
    private BufferedReader input;
    private BufferedWriter toServer;

    public HumanPlayerSend(Socket server) {
        this.server = server;
        try {
            input = new BufferedReader(new InputStreamReader(System.in));
            toServer = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));

            writeToServer("human");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        String action;
        try {
            while ((action = input.readLine()) != null) {
                writeToServer(action);
            }
         } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("closing...");
            System.exit(0);
        }
    }

    public void writeToServer(String message) {
        try {
            System.out.println("from you: " + message);
            toServer.write(message);
            toServer.newLine();
            toServer.flush();
        } catch (SocketException e) {
            System.out.println("Server unexpectedly disconnected.");
            //System.exit(0);
        } catch (IOException e) {
            System.out.println("hmmmm");
            e.printStackTrace();
        }
    }
}
