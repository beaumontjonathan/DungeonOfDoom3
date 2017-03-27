import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import static java.lang.Thread.sleep;

public class HumanClientGUI extends JFrame {

    private static final long serialVersionUID = 8448144773615962056L;
    private HumanClientController controller;
    private MapGrid mapGrid = null;
    private MessageBox messageBox;
    private JComboBox<String> usernamesComboBox;
    private JLabel goldLabel;
    private ActionStatus actionStatus;
    private JButton pickupGoldButton;
    private JButton editUsernameButton;
    private JButton quitButton;
    private ArrowButton nButton;
    private ArrowButton wButton;
    private ArrowButton eButton;
    private ArrowButton sButton;
    private JComboBox<String> toOption;
    private JButton sendButton;
    private PlaceholderTextArea messageInput;

    public HumanClientGUI(HumanClientController controller) {
        super("Dungeon of Doom");
        this.controller = controller;
        instantiateComponents();

        //ConnectToServerGUI connectToServerGUI = new ConnectToServerGUI(controller);

    }

    public void instantiateComponents() {
        messageBox = new MessageBox();
        messageInput = new PlaceholderTextArea("Write something...", 3, 20);
        goldLabel = new JLabel();
        usernamesComboBox = new JComboBox<>();
        actionStatus = new ActionStatus(15);
        pickupGoldButton = new JButton("Pickup");
        editUsernameButton = new JButton("Edit username");
        quitButton = new JButton("Quit");
        toOption = new JComboBox<>();
        sendButton = new JButton("Send");
    }

	public static void main(String[] args) {
        new HumanClientController().startConnectGUI();
	}

    public void createAndShowGUI() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                controller.exitGame();
            }
        });int n = 0;
        while(mapGrid == null) {
            try {sleep(20);//System.out.println(++n);
                } catch (Exception e) {};
        }
        this.addComponentsToPane(this.getContentPane());
        this.pack();
        //https://stackoverflow.com/questions/2442599/how-to-set-jframe-to-appear-centered-regardless-of-monitor-resolution
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        this.setVisible(true);
    }

    private void addComponentsToPane(Container pane) {
        JPanel pageHeader = getPageHeader();
        JPanel centrePanel = getCentrePanel();
        JPanel chatBar = getChatBar();

        pane.add(pageHeader, BorderLayout.PAGE_START);
        pane.add(centrePanel, BorderLayout.CENTER);
        pane.add(chatBar, BorderLayout.LINE_START);
    }

    private JPanel getPageHeader() {
        JPanel headerPanel = new JPanel();
        JLabel title = new JLabel("<html><h1>Dungeon of Doom</h1></html>", SwingConstants.CENTER);
        headerPanel.add(title);
        return headerPanel;
    }

    private JPanel getCentrePanel() {
        JPanel centrePanel = new JPanel(new BorderLayout());
        JPanel navButtons = getNavButtons();

        JPanel editAndQuitButtonContainer = new JPanel();
        editAndQuitButtonContainer.setLayout(new BoxLayout(editAndQuitButtonContainer, BoxLayout.X_AXIS));
        editAndQuitButtonContainer.add(editUsernameButton);
        editAndQuitButtonContainer.add(Box.createHorizontalGlue());
        editAndQuitButtonContainer.add(quitButton);
        editUsernameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newUsername = JOptionPane.showInputDialog("New username");
                if (newUsername != null && !newUsername.equals(""))
                    controller.processUpdateUsernameOutput(newUsername);
            }
        });
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int dialogResult = JOptionPane.showConfirmDialog (null, "Would you really like to quit?","Warning",JOptionPane.YES_NO_OPTION);
                if(dialogResult == JOptionPane.YES_OPTION){
                    controller.exitGame();
                }
            }
        });


        JPanel goldPanel = new JPanel();
        goldPanel.setLayout(new BoxLayout(goldPanel, BoxLayout.X_AXIS));
        goldPanel.add(goldLabel);
        goldPanel.add(Box.createHorizontalGlue());
        goldPanel.add(pickupGoldButton);
        goldPanel.setMaximumSize(new Dimension(300, 100));

        pickupGoldButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.processPickupOutput();
            }
        });

        JPanel actionStatusContainer = new JPanel();
        actionStatusContainer.setLayout(new BoxLayout(actionStatusContainer, BoxLayout.LINE_AXIS));
        actionStatusContainer.add(Box.createHorizontalGlue());
        actionStatusContainer.add(actionStatus);

        JPanel centrePanelInner = new JPanel();
        centrePanelInner.setLayout(new BoxLayout(centrePanelInner, BoxLayout.Y_AXIS));
        centrePanelInner.add(editAndQuitButtonContainer);
        centrePanelInner.add(mapGrid);
        centrePanelInner.add(goldPanel);
        centrePanelInner.add(navButtons);
        centrePanelInner.add(actionStatusContainer);
        JScrollPane scrollPane = new JScrollPane(centrePanelInner);

        centrePanel.add(scrollPane);
        return centrePanel;
    }

    private JPanel getNavButtons() {
        // TODO: nicer action listeners

        JPanel navButtonsPanel = new JPanel();
        navButtonsPanel.setLayout(new BoxLayout(navButtonsPanel, BoxLayout.Y_AXIS));

        try {
            nButton = new ArrowButton('N');
            wButton = new ArrowButton('W');
            eButton = new ArrowButton('E');
            sButton = new ArrowButton('S');
        } catch (InvalidDirectionException e) {
            e.printStackTrace();
            System.exit(1);
        }

        nButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        sButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel eastAndWestButtonContainer = new JPanel();
        eastAndWestButtonContainer.setLayout(new BoxLayout(eastAndWestButtonContainer, BoxLayout.X_AXIS));
        eastAndWestButtonContainer.add(wButton);
        eastAndWestButtonContainer.add(Box.createHorizontalGlue());
        eastAndWestButtonContainer.add(eButton);

        navButtonsPanel.setMaximumSize(new Dimension(150, 150));
        navButtonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        navButtonsPanel.add(nButton);
        navButtonsPanel.add(eastAndWestButtonContainer);
        navButtonsPanel.add(sButton);

        nButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.processMoveOutput('N');
            }
        });
        wButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.processMoveOutput('W');
            }
        });
        eButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.processMoveOutput('E');
            }
        });
        sButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.processMoveOutput('S');
            }
        });

        return navButtonsPanel;
    }

    private JPanel getChatBar() {
        JPanel chatBarPanel = new JPanel();
        chatBarPanel.setLayout(new BorderLayout());

        // topMenu
        JPanel topMenu = new JPanel();
        topMenu.setLayout(new BoxLayout(topMenu, BoxLayout.Y_AXIS));

        // topMenuOptions
        JPanel topMenuOptions = new JPanel();
        topMenuOptions.setLayout(new BoxLayout(topMenuOptions, BoxLayout.X_AXIS));

        // to<all/username>
        toOption.addItem("All");
        toOption.addItem("User");
        toOption.setAlignmentX(Component.CENTER_ALIGNMENT);
        toOption.setPreferredSize(new Dimension(35, 30));

        // usernames
        usernamesComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernamesComboBox.setPreferredSize(new Dimension(110, 30));
        usernamesComboBox.setEnabled(false);


        topMenuOptions.add(toOption);
        topMenuOptions.add(Box.createHorizontalGlue());
        topMenuOptions.add(usernamesComboBox);

        // message input
        messageInput.setLineWrap(true);
        JScrollPane messageInputScrollPane = new JScrollPane(messageInput);

        // send button
        JPanel sendWrapper = new JPanel();
        sendWrapper.setLayout(new BoxLayout(sendWrapper, BoxLayout.X_AXIS));
        sendWrapper.add(sendButton);

        topMenu.add(topMenuOptions);
        topMenu.add(messageInputScrollPane);
        topMenu.add(sendWrapper);

        messageBox.addMessage("Welcome to Dungeon of Dooom!!");
        messageBox.setPreferredSize(new Dimension(100, 100));
        final JScrollPane chatBoxScrollPane = new JScrollPane(messageBox);
        chatBoxScrollPane.revalidate();
        chatBarPanel.add(topMenu, BorderLayout.PAGE_START);
        chatBarPanel.add(chatBoxScrollPane, BorderLayout.CENTER);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mapGrid.revalidate();
                // TODO: send null stuff?
                String message = messageInput.getMessage().trim().replaceAll("[\t\n]", "");
                if (!message.equals("")) {
                    String messageToOption = toOption.getSelectedItem().toString();
                    Object usernameOption = usernamesComboBox.getSelectedItem();
                    if (messageToOption.equals("All") || (messageToOption.equals("User") && usernameOption != null)) {
                        String username = messageToOption.equals("All") ? null : usernameOption.toString();
                        controller.processChatMessageOutput(message, username);
                        chatBoxScrollPane.revalidate();
                    }
                }
            }
        });

        messageInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String message = messageInput.getText().trim().replaceAll("[\t\n]", "");
                    if (!message.equals("")) {
                        String messageToOption = toOption.getSelectedItem().toString();
                        Object usernameOption = usernamesComboBox.getSelectedItem();
                        if (messageToOption.equals("All") || (messageToOption.equals("User") && usernameOption != null)) {
                            String username = messageToOption.equals("All") ? null : usernameOption.toString();
                            controller.processChatMessageOutput(message, username);
                            chatBoxScrollPane.revalidate();
                        }
                    }
                    e.consume();
                    messageInput.setText("");
                } else if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    e.consume();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        toOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                usernamesComboBox.setEnabled(!toOption.getSelectedItem().toString().equals("All"));
            }
        });



        return chatBarPanel;
    }

    public void addChatMessage(String message) {
        messageBox.addMessage(message);
    }

    public void addChatMessage(String message, Color color) {
        messageBox.addMessage(message, color);
    }

    public void addUsername(String username) {
        System.out.println("username is " + username);
        usernamesComboBox.addItem(username);
        usernamesComboBox.revalidate();
    }

    public void removeUsername(String username) {
        usernamesComboBox.removeItem(username);
        usernamesComboBox.revalidate();
    }

    public void updateUsername(String oldUsername, String newUsername) {
        removeUsername(oldUsername);
        addUsername(newUsername);
    }

    private static String[] getHostAndPort() {
        String[] hostAndPort = new String[2];

        JTextField hostNameField = new JTextField(8);
        JTextField portNumberField = new JTextField(3);

        JPanel panel = new JPanel();
        panel.add(new JLabel("Hostname:"));
        panel.add(hostNameField);
        panel.add(Box.createHorizontalStrut(15));
        panel.add(new JLabel("Port:"));
        panel.add(portNumberField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Connect to DOD", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            hostAndPort[0] = hostNameField.getText();
            hostAndPort[1] = portNumberField.getText();
        }
        hostAndPort[0] = (hostAndPort[0] == null) ? "" : hostAndPort[0];
        hostAndPort[1] = (hostAndPort[1] == null) ? "" : hostAndPort[1];
        return hostAndPort;
    }

    // MapGrid stuff

    public synchronized void instantiateMapGrid(int width) {
        mapGrid = new MapGrid(width, width, 50);
    }

    public synchronized void updateRow(int n, char[] row) {
        mapGrid.insertMapRow(n, row);
    }

    public void updateGoldRequired(int gold) {
        goldLabel.setText("Gold required: " + gold);
        goldLabel.revalidate();
    }

    public void pickupGold() {
        int goldRequired = Integer.parseInt(goldLabel.getText().replace("Gold required: ", ""));
        goldRequired = (goldRequired == 0) ? 0 : --goldRequired;
        System.out.println("gold required: " + goldRequired);
        updateGoldRequired(goldRequired);
    }

    public void disconnectGUI() {
        pickupGoldButton.setEnabled(false);
        messageInput.setEditable(false);
        messageInput.setEnabled(false);
        editUsernameButton.setEnabled(false);
        usernamesComboBox.setEnabled(false);
        nButton.setEnabled(false);
        eButton.setEnabled(false);
        sButton.setEnabled(false);
        wButton.setEnabled(false);
        toOption.setEnabled(false);
        sendButton.setEnabled(false);
    }

    public void actionSuccessful(boolean successful) {
        actionStatus.actionSuccessful(successful);
    }

    public void exit() {
        setVisible(false);
        dispose();
    }
}
