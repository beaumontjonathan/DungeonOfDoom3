import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class HumanPlayerReceive implements Runnable {

    private Socket server;
    private BufferedReader fromServer;
    private HumanClientController controller;

    public HumanPlayerReceive(Socket server, HumanClientController controller) {
        this.server = server;
        this.controller = controller;
        try {
            fromServer = new BufferedReader(new InputStreamReader(server.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        String fromServer;
        while ((fromServer = readLineFromServer()) != null) {
            controller.processServerOutput(fromServer);
        }
    }

    public String readLineFromServer() {
        String fromServer = null;
        try {
            fromServer = this.fromServer.readLine();
        } catch (SocketException e) {

            System.out.println("Server unexpectedly disconnected.");
            controller.processServerOutput("Server unexpectedly disconnected.");
            try {
                server.close();
            } catch (IOException e2) {
                System.out.println("trying to close socket?");
            }
        } catch (IOException e) {
            System.err.println("IO error reading line from server.");
        }
        return fromServer;
    }
}
