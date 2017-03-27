import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by Beaum on 17/03/2017.
 */
public class ConnectToServerGUI extends JFrame {

    private static final long serialVersionUID = -8467475590799696141L;
    private JLabel errorLabel;
    private boolean cancelPressed;
    private HumanClientController controller;

    public ConnectToServerGUI(HumanClientController controller) {
        super("Connect to Dungeon of Doom");
        errorLabel = new JLabel();
        cancelPressed = false;
        this.controller = controller;
    }

    public void createAndShowGUI() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.out.println("Closing?");
            }
        });
        this.addComponentsToPane(this.getContentPane());
        this.pack();
        //https://stackoverflow.com/questions/2442599/how-to-set-jframe-to-appear-centered-regardless-of-monitor-resolution
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        this.setResizable(false);
        this.setVisible(true);
    }

    public void addComponentsToPane(Container pane) {
        JPanel page = new JPanel();
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));

        JPanel titlePanel = new JPanel();
        JLabel title = new JLabel("<html><h3>Connect to the DoD server</h3></html>");
        titlePanel.add(title);

        JPanel menuLayout = new JPanel(new FlowLayout());
        menuLayout.add(new JLabel("Hostname:"));
        final JTextField hostname = new JTextField();
        hostname.setPreferredSize(new Dimension(100, 20));
        menuLayout.add(hostname);
        menuLayout.add(new JLabel("Port:"));
        final JTextField port = new JTextField();
        port.setPreferredSize(new Dimension(40, 20));
        menuLayout.add(port);
        menuLayout.setBorder(new EmptyBorder(10, 10, 10, 10));



        JPanel errorContainer = new JPanel();
        errorLabel.setForeground(Color.RED);
        errorContainer.add(errorLabel);

        JPanel connectContainer = new JPanel(new FlowLayout());
        JButton connectButton = new JButton("Connect");
        connectButton.setPreferredSize(new Dimension(85, 20));
        connectContainer.add(connectButton);

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                cancelPressed = false;
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        connect(hostname.getText().trim(), port.getText().trim());
                    }
                });
            }
        });

        JButton quitButton = new JButton("Quit");
        quitButton.setPreferredSize(new Dimension(80, 20));
        connectContainer.add(quitButton);

        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                quit();
            }
        });


        page.add(titlePanel);
        page.add(errorContainer);
        page.add(menuLayout);
        page.add(connectContainer);
        pane.add(page, BorderLayout.CENTER);
    }

    private void connect(String hostName, String portNumber) {
        String error = controller.attemptConnection(hostName, portNumber);
        if (error == null) {
            this.dispose();
        } else {
            this.setVisible(true);
            System.out.println(error);
            displayError(error);
        }
    }

    private void displayError(String error) {
        errorLabel.setText(error);
        errorLabel.revalidate();

        Dimension beforePackSize = getSize();
        pack();
        setSize(new Dimension(beforePackSize.width, getHeight()));
    }

    private void quit() {
        if (cancelPressed) {
            System.exit(0);
        } else {
            displayError("Press quit again to exit");
            cancelPressed = true;
        }
    }

}
