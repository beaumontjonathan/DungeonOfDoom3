import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

/**
 * Produces an arrow shaped button.
 *
 * @author Jonathan Beaumont
 */
public class ArrowButton extends JButton {

    private static final long serialVersionUID = 5390515140812418112L;
    private Polygon shape;
    private static final HashMap<Character, Integer[][]> TRIANGLE_COORDINATES = new HashMap<Character, Integer[][]>() {
        private static final long serialVersionUID = -4471808136694367835L;
        {
        put ('N', new Integer[][] {{20, 20, 0, 25, 50, 30, 30}, {50, 25, 25, 0, 25, 25, 50}});
        put ('W', new Integer[][] {{25, 0, 25, 25, 50, 50, 25}, {0, 25, 50, 30, 30, 20, 20}});
        put ('E', new Integer[][] {{25, 50, 25, 25, 0, 0, 25}, {0, 25, 50, 30, 30, 20, 20}});
        put ('S', new Integer[][] {{20, 20, 0, 25, 50, 30, 30}, {0, 25, 25, 50, 25, 25, 0}});
    }};
    private static final Color NORMAL_COLOR = Color.BLACK;
    private static final Color HOVER_COLOR = Color.DARK_GRAY;
    private static final Color PRESS_COLOR = Color.GRAY;

    /**
     * Constructor. Sets up the direction of the arrow and set the
     * formatting of the button.
     *
     * @param   direction   if the direction for the arrow to point in.
     * @throws  InvalidDirectionException   if the direction is not N/E/S/W
     */
    public ArrowButton(char direction) throws InvalidDirectionException {

        if (isValidDirection(direction)) {
            // Gets the x and y points for the arrow
            int[] xPoints = convertIntegerArrayToPrimitive(TRIANGLE_COORDINATES.get(direction)[0]);
            int[] yPoints = convertIntegerArrayToPrimitive(TRIANGLE_COORDINATES.get(direction)[1]);

            // Sets the shape of the button
            this.shape = new Polygon(xPoints, yPoints, 7);

            // Sets up and forces the size
            this.setSize(new Dimension(50, 50));
            this.setMinimumSize(this.getSize());
            this.setMaximumSize(this.getSize());
            this.setPreferredSize(this.getSize());

            // Sets up the background and stops
            this.setForeground(NORMAL_COLOR);
            this.setBorderPainted(false);
            this.setOpaque(false);
            this.setContentAreaFilled(false);

        } else {
            // If the direction is not valid
            throw new InvalidDirectionException(direction);
        }

        addMouseListener(new ArrowButtonMouseListener());
    }

    /**
     * @param direction is a char direction
     * @return  if the direction is one of N/E/S/W
     */
    private boolean isValidDirection(char direction) {
        return TRIANGLE_COORDINATES.containsKey(direction);
    }

    /**
     * Paints the arrow shaped polygon when the background is being
     * changed.
     *
     * @param g graphics object to edit
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D gCopy = (Graphics2D) g.create();
        gCopy.fillPolygon(this.shape);
        g.dispose();
    }

    @Override
    public boolean contains(int x, int y) {
        return this.shape.contains(x, y);
    }

    @Override
    protected void paintBorder(Graphics g) {

    }

    private int[] convertIntegerArrayToPrimitive(Integer[] objectArray) {
        int[] primitiveArray = new int[objectArray.length];
        for (int i = 0; i < objectArray.length; i++)
            primitiveArray[i] = objectArray[i];
        return primitiveArray;
    }

    private void enableNormalColor() {
        setForeground(NORMAL_COLOR);
    }

    private void enableHoverColor() {
        if (isEnabled())
            setForeground(HOVER_COLOR);
        else
            enableNormalColor();
    }

    private void enablePressColor() {
        if (isEnabled())
            setForeground(PRESS_COLOR);
        else
            enableNormalColor();
    }

    class ArrowButtonMouseListener implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {
            enablePressColor();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            enableHoverColor();
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            enableHoverColor();
        }

        @Override
        public void mouseExited(MouseEvent e) {
            enableNormalColor();
        }
    }
}



class InvalidDirectionException extends Exception {
    public InvalidDirectionException(char direction) {
        super("Unrecognised direction : " + direction);
    }
}
