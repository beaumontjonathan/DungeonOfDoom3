import java.awt.*;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import static java.lang.Thread.sleep;

public class HumanClientController {

    private Socket server;
    private HumanPlayerSend send;
    private HumanPlayerReceive receive;
    private int line;
    private HumanClientGUI gui;
    private boolean socketConnected;
    private int mapWidth;
    private boolean mapGridBuilt;
    private boolean gameOver;

    public HumanClientController() {
        init();
    }

    private void init() {
        line = 0;
        socketConnected = false;
        mapGridBuilt = false;
        gameOver = false;
    }

    public void startConnectGUI() {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ConnectToServerGUI(HumanClientController.this).createAndShowGUI();
            }
        });
    }



    public String attemptConnection(String hostName, String portNumber) {
        if (hostName.equals("")) {
            return "Enter a valid hostname";
        } else if (!isValidPort(portNumber)) {
            return "Enter a valid port";
        }
        try {
            server = new Socket(hostName, Integer.parseInt(portNumber));
            System.out.println("connected to server");
            socketConnected = true;

            send = new HumanPlayerSend(server);
            receive = new HumanPlayerReceive(server, this);

            String serverResponse = receive.readLineFromServer();
            switch (serverResponse) {
                case "Welcome to DOD":
                    gameInit();
                    return null;
                case "Port unavailable":
                    return serverResponse;
                default:
                    return "Unknown error";
            }

        } catch (UnknownHostException e) {
            return "Unknown hostname";
        } catch (ConnectException e) {
            return "Unable to connect to server";
        } catch (IOException e) {
            return "Error";
        }
    }

    private boolean isValidPort(String port) {
        if (port == null) {
            return false;
        } else if (
                port.equals("") ||
                        !port.matches("^[0-9]{1,6}+$") ||
                        Integer.parseInt(port) > 65536
                ) {
            return false;
        }
        return true;
    }

    private void gameInit() {
        startMainGUI();
        new Thread(send).start();
        new Thread(receive).start();
        writeToServer("LOOK");
        writeToServer("HELLO");
        writeToServer("USERNAMES");
    }

    private void startMainGUI() {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                gui = new HumanClientGUI(HumanClientController.this);
                gui.createAndShowGUI();
            }
        });
    }

    public void exitGame() {
        writeToServer("QUIT");
        gui.exit();
    }


    private void writeToServer(String output) {
        if (socketConnected && !gameOver)
            send.writeToServer(output);
    }

    public void processLookThreadAction() {
        writeToServer("LOOK");
    }

    public void processChatMessageOutput(String message, String username) {
        String output;
        if (username == null)
            output = "SHOUT ";
        else
            output = "WHISPER " + username + " ";
        output += message;
        writeToServer(output);
    }

    public void processMoveOutput(char direction) {
        final char[] directions = {'N', 'E', 'S', 'W'};
        if(Arrays.asList(direction).contains(direction)) {
            writeToServer("MOVE " + direction);
            writeToServer("LOOK");
        }
    }

    public void processUpdateUsernameOutput(String newUsername) {
        if (isValidUsername(newUsername)) {
            writeToServer("USERNAME " + newUsername);
        }
    }

    public void processPickupOutput() {
        writeToServer("PICKUP");
    }




    public void processServerOutput(String input) {

        System.out.println("from server: " + input);
        if (processLookInputCommand(input)) {
        } else if (processMessageInputCommand(input)) {
        } else if (processUsernameUpdateInputCommand(input)) {
        } else if (processPlayerUsernameUpdateInputCommand(input)) {
        } else if (processNewPlayerInputCommand(input)) {
        } else if (processPlayerLeftInputCommand(input)) {
        } else if (processUsernamesInputCommand(input)) {
        } else if (processHelloInputCommand(input)) {
        } else if (processPickupInputCommand(input)) {
        } else if (processQuitInputCommand(input)) {
        } else if (gameOver) {
            gui.addChatMessage(input, Color.red);
        } else if (input.equals("FAIL")) {
            gui.actionSuccessful(false);
        } else if (input.equals("SUCCESS")) {
            gui.actionSuccessful(true);
        }
    }

    private boolean processLookInputCommand(String command) {
        if (command.matches("^[HBGE.#]+$")) {
            if (!mapGridBuilt && line == 0) {
                gui.instantiateMapGrid(command.length());
                mapWidth = command.length();
            }
            gui.updateRow(line, command.toCharArray());
            if (!mapGridBuilt && line == 4)
                mapGridBuilt = true;
            line = (line == mapWidth - 1) ? 0 : ++line;
            return true;
        } else {
            return false;
        }
    }

    private boolean processMessageInputCommand(String command) {
        String[] words = command.split(" ");
        if (words.length < 4) {
            return false;
        } else if (
                (isValidUsername(words[0]) || words[0].equals("YOU")) &&
                        words[1].equals("(TO") &&
                        (words[2].equals("YOU):") || words[2].equals("ALL):") || isValidUsername(words[2].replace("):", ""))) &&
                        !words[3].equals("")
                ) {
            gui.addChatMessage(command);
            return true;
        } else {
            return false;
        }
    }

    private boolean processUsernameUpdateInputCommand(String command) {
        String[] words = command.split(" ");
        if (words.length == 5) {
            if (
                    words[0].equals("USERNAME:") &&
                            isValidUsername(words[1]) &&
                            (words[2] + words[3]).equals("UPDATEDTO:") &&
                            isValidUsername(words[4])
                    ) {
                gui.updateUsername(words[1], words[4]);
                gui.addChatMessage(command, Color.blue);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean processPlayerUsernameUpdateInputCommand(String command) {
        String[] words = command.split(" ");
        if (
                words.length == 3 &&
                command.startsWith("USERNAME CHANGED: ") &&
                isValidUsername(words[2])
                ) {
            gui.addChatMessage(command, Color.blue);
            return true;
        } else if (
                words.length == 3 &&
                command.startsWith("USERNAME UNCHANGED: ") &&
                isValidUsername(words[2])
                ) {
            gui.addChatMessage(command, Color.blue);
            return true;
        } else if (
                words.length == 3 && command.startsWith("INVALID USERNAME: ")) {
            gui.addChatMessage(command, Color.blue);
            return true;
        } else if (command.equals("MAXIMUM USERNAME LENGTH 14")) {
            gui.addChatMessage(command, Color.blue);
            return true;
        } else {
            return false;
        }
    }

    private boolean processNewPlayerInputCommand(String command) {
        if (command.equals("NEW BOT ADDED!!")) {
            gui.addChatMessage(command, Color.red);
            return true;
        }
        String[] words = command.split(" ");
        if (words.length == 3) {
            if (command.startsWith("NEW PLAYER: ") && isValidUsername(words[2])) {
                gui.addUsername(words[2]);
                gui.addChatMessage(command, Color.green);
                return true;
            }
        }
        return false;
    }

    private boolean processPlayerLeftInputCommand(String command) {
        if (command.equals("A BOT HAS LEFT THE GAME...")) {
            gui.addChatMessage(command, Color.red);
            return true;
        }
        String[] words = command.split(" ");
        if (command.startsWith("PLAYER EXIT: ")) {
            gui.removeUsername(words[2]);
            gui.addChatMessage(command.replace("PLAYER EXIT: ", ""), Color.green);
            return true;
        }
        return false;
    }

    private boolean processUsernamesInputCommand(String command) {
        String[] outputLines = command.split("\t");
        if (outputLines.length >= 1) {
            if (outputLines[0].matches("^[0-9]+ OTHER PLAYERS?+ ACTIVE:$")) {
                int numberOfUsernames = Integer.parseInt(outputLines[0].split(" ")[0]);
                for (int i = 1; i < outputLines.length; i++) {
                    String line = outputLines[i];
                    String username = line.split("-")[1];
                    gui.addUsername(username);
                }
                return true;
            }
        }
        return false;
    }

    private boolean processHelloInputCommand(String command) {
        if (command.matches("^GOLD: [0-9]+$")) {
            int gold = Integer.parseInt(command.replace("GOLD: ", ""));
            gui.updateGoldRequired(gold);
            return true;
        }
        return false;
    }

    private boolean processPickupInputCommand(String command) {
        if (command.matches("^GOLD COINS: [0-9]+$")) {
            gui.pickupGold();
            gui.actionSuccessful(true);
            return true;
        } else if (command.equals("There is nothing to pick up...")) {
            gui.actionSuccessful(false);
            return true;
        }
        return false;
    }

    private boolean processQuitInputCommand(String command) {
        if (command.equals("Server unexpectedly disconnected.") || command.equals("bye bye")) {
            socketConnected = false;
            gui.addChatMessage(command, Color.red);
            gui.actionSuccessful(false);
            gameOver = true;
            gui.disconnectGUI();
            return true;
        }
        return false;
    }

    private boolean isValidUsername(String username) {
        if (username.matches("^[a-zA-Z]+[a-zA-Z0-9]*$") ||
                username.matches("^PLAYER_[0-9]+$"))
            return true;
        else
            return false;
    }

    public boolean getGameOver() {
        return gameOver;
    }

}