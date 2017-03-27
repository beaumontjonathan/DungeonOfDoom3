import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.*;

public class DoDServerClientListener implements Runnable {

    private ServerSocket serverSocket;
    private GameLogic game;
    private static Integer counter;
    private boolean acceptNewClients;
    private int portNumber;

    public DoDServerClientListener(GameLogic game, int portNumber) {
        this.game = game;
        if (counter == null) {
            counter = 0;
        }
        acceptNewClients = false;
        this.portNumber = portNumber;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void startServerSocket() throws IOException {
        serverSocket = new ServerSocket(portNumber);
    }

    public void stopServerSocket() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("server socket closed, io exception caught...");
        }
    }

    public void startAcceptingClients() {
        acceptNewClients = true;
        System.out.println("Server : Listening for Clients on port " + portNumber);
    }

    public void stopAcceptingClients() {
        acceptNewClients = false;
        System.out.println("Server : Stopped listening for Clients on port " + portNumber);
    }

    public void run() {
        while (!serverSocket.isClosed()) {
            try {
                Socket clientSocket = serverSocket.accept();
                if (acceptNewClients) {
                    int playerID = getNewID();
                    System.out.println("Server : Client Accepted (" + playerID + ")");
                    game.addPlayer(new Player(clientSocket, game, playerID));
                } else {
                    rejectNewSocket(clientSocket);
                }
            } catch (SocketException e) {
                System.out.println("Server : Stopped listening for Clients");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized int getNewID() {
        int id = counter;
        counter++;
        return id;
    }

    private void rejectNewSocket(Socket clientSocket) {
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            out.write("Port unavailable");
            out.newLine();
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
