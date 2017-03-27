import java.io.IOException;
import java.util.*;

public class DoDServerController {

    private boolean serverRunning;
    private GameLogic game;
    private DoDServerGUI serverGUI;
    private DoDServerClientListener currentActiveClientListener;
    private HashMap<Integer, DoDServerClientListener> clientListenerHashMap;

    public DoDServerController() {
        init();
    }

    private int getCurrentActivePort() {
        return currentActiveClientListener.getPortNumber();
    }

    private void init() {
        clientListenerHashMap = new HashMap<>();
        serverRunning = false;
        game = new GameLogic(this);
    }

    public void startGame() {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DoDServerStartGUI(DoDServerController.this).createAndShowGUI();
            }
        });
    }

    private void updateCurrentClientListener(DoDServerClientListener clientListener) {
        if (currentActiveClientListener != null)
            currentActiveClientListener.stopAcceptingClients();
        currentActiveClientListener = clientListener;
        currentActiveClientListener.startAcceptingClients();
    }

    private boolean portUsed(int portNumber) {
        return clientListenerHashMap.containsKey(portNumber);
    }

    private void setupNewClientListener(DoDServerClientListener newClientListener) {
        updateCurrentClientListener(newClientListener);
        clientListenerHashMap.put(getCurrentActivePort(), newClientListener);
        new Thread(newClientListener).start();
    }

    public String attemptServerStart(String portString) {
        if (isValidPort(portString)) {
            int portNumber = Integer.parseInt(portString);
            if (portUsed(portNumber)) {
                if (portNumber == getCurrentActivePort()) {
                    return "Port already active";
                } else {
                    updateCurrentClientListener(clientListenerHashMap.get(portNumber));
                    return null;
                }
            } else {

                try {
                    DoDServerClientListener newClientListener = new DoDServerClientListener(game, portNumber);
                    newClientListener.startServerSocket();
                    setupNewClientListener(newClientListener);


                    if (!serverRunning) {
                        startMainGui();
                        serverRunning = true;
                    } else {
                        serverGUI.updatePortLabel(portString);
                    }

                    return null;
                } catch (IOException e) {
                    return "Cannot start server";
                }
            }
        } else {
            return "Invalid port";
        }
    }



    private void startMainGui() {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                serverGUI = new DoDServerGUI(DoDServerController.this, game, getCurrentActivePort());
                serverGUI.createAndShowGUI();
            }
        });
    }

    private boolean isValidPort(String portNumber) {
        if (portNumber == null) {
            return false;
        } else if (
                portNumber.equals("") ||
                        !portNumber.matches("^[0-9]{1,6}+$") ||
                        Integer.parseInt(portNumber) > 65536
                ) {
            return false;
        }
        return true;
    }

    public void updateMapGrid() {
        serverGUI.updateMapGrid();
    }

    public void endGame() {
        game.endGame();
        currentActiveClientListener.stopServerSocket();
         Collection<DoDServerClientListener> list = clientListenerHashMap.values();
         Iterator<DoDServerClientListener> iterator = list.iterator();
         while (iterator.hasNext()) {
             DoDServerClientListener clientListener = iterator.next();
             clientListener.stopServerSocket();
         }
        init();
        startGame();
    }

}
