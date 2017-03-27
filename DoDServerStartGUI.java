import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by Beaum on 24/03/2017.
 */
public class DoDServerStartGUI extends JFrame {
    private static final long serialVersionUID = 4022618435398135838L;
    private JLabel errorLabel;
    private boolean cancelPressed;
    private DoDServerController controller;

    public DoDServerStartGUI(DoDServerController controller) {
        super("Start DoD server");
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
                System.out.println("Closing.?");
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
        JLabel title = new JLabel("<html><h3>Start the DoD server</h3></html>");
        titlePanel.add(title);

        JPanel menuLayout = new JPanel(new FlowLayout());
        menuLayout.add(new JLabel("Port:"));
        final JTextField port = new JTextField();
        port.setPreferredSize(new Dimension(40, 20));
        menuLayout.add(port);
        menuLayout.setBorder(new EmptyBorder(10, 10, 10, 10));



        JPanel errorContainer = new JPanel();
        errorLabel.setForeground(Color.RED);
        errorContainer.add(errorLabel);

        JPanel startContainer = new JPanel(new FlowLayout());
        JButton startButton = new JButton("Start");
        startButton.setPreferredSize(new Dimension(85, 20));
        startContainer.add(startButton);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                cancelPressed = false;
                startGame(port.getText().trim());
            }
        });

        JButton quitButton = new JButton("Quit");
        quitButton.setPreferredSize(new Dimension(80, 20));
        startContainer.add(quitButton);

        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                quit();
            }
        });


        page.add(titlePanel);
        page.add(errorContainer);
        page.add(menuLayout);
        page.add(startContainer);
        pane.add(page, BorderLayout.CENTER);
    }

    private void startGame(String portNumber) {
        String error = controller.attemptServerStart(portNumber);
        if (error != null) {
            this.setVisible(true);
            System.out.println(error);
            displayError(error);
        } else {
            dispose();
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
