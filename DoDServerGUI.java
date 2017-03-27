import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class DoDServerGUI extends JFrame {

    private static final long serialVersionUID = -5056371061826781622L;
    private DoDServerController controller;
	private GameLogic game;
	private MapGrid mapGrid;
	private String hostName;
    private JScrollPane scrollPane;
    private JLabel portLabel;

    public static void main(String[] args) {
        new DoDServerController().startGame();
	}
	
	public DoDServerGUI(DoDServerController controller, GameLogic game, int port) {
		//serverSocket = new ServerSocket(portNumber);
        super("Dungeon of Doom Server");
        this.controller = controller;
        this.game = game;
        this.hostName = getHostName();
        portLabel = new JLabel("Port: " + port);
	}

	private String getHostName() {
        String hostname;
        try {
            hostname = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e){
	        hostname = "Unknown hostname";
        }
        return hostname;
    }

	public void createAndShowGUI() {
        //new Thread(new DoDServerClientListener(serverSocket, game)).start();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.out.println("closing server...");
            }
        });
        this.addComponentsToPane(this.getContentPane());
        this.pack();
        //https://stackoverflow.com/questions/2442599/how-to-set-jframe-to-appear-centered-regardless-of-monitor-resolution
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        this.setVisible(true);
    }

    public void updateMapGrid() {
	    char[][] currentMap = game.getPopulatedMap();
	    if (mapGrid != null)
	        mapGrid.insertCharMap(currentMap);
    }

    private void addComponentsToPane(final Container pane) {
	    char[][] map = game.getPopulatedMap();
	    mapGrid = new MapGrid(map.length, map[0].length, 30);
	    mapGrid.insertCharMap(map);
        scrollPane = new JScrollPane(mapGrid);

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("<html><h2>DoD Server</h2></html>");
        JPanel titleContainer = new JPanel();
        titleContainer.add(title);
        header.add(titleContainer);


        JPanel hostAndPortContainer = new JPanel();
        hostAndPortContainer.setLayout(new BoxLayout(hostAndPortContainer, BoxLayout.X_AXIS));
        hostAndPortContainer.setBorder(new EmptyBorder(0, 50, 10, 50));
        String host = "Hostname: " + hostName;
        JLabel hostLabel = new JLabel(host);
        hostAndPortContainer.add(hostLabel);
        hostAndPortContainer.add(Box.createHorizontalGlue());
        hostAndPortContainer.add(portLabel);

        JButton editPort = new JButton("Edit port");
        hostAndPortContainer.add(editPort);

        editPort.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newPortNumber = JOptionPane.showInputDialog("New port");
                if (newPortNumber != null && !newPortNumber.equals("")) {
                    String result = controller.attemptServerStart(newPortNumber);
                    if (result != null) {
                        JOptionPane.showMessageDialog(pane, result, "Error", JOptionPane.ERROR_MESSAGE);
                        System.out.println(result);
                    }
                }
            }
        });

        header.add(hostAndPortContainer);

        final JButton button = new JButton("Hide");

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                button.setText(button.getText().equals("Hide") ? "Show" : "Hide");
                scrollPane.setVisible(!scrollPane.isVisible());
                Dimension beforePackSize = getSize();
                pack();
                setSize(new Dimension(beforePackSize.width, getHeight()));

            }
        });

        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int dialogResult = JOptionPane.showConfirmDialog (null, "Would you really like to quit?","Warning",JOptionPane.YES_NO_OPTION);
                if(dialogResult == JOptionPane.YES_OPTION){
                    setVisible(false);
                    dispose();
                    controller.endGame();
                }
            }
        });
        header.add(button);
        header.add(quitButton);
        pane.add(header, BorderLayout.PAGE_START);
	    pane.add(scrollPane, BorderLayout.CENTER);
    }

    public void updatePortLabel(String newPort) {
        portLabel.setText("Port: " + newPort);
    }
}
