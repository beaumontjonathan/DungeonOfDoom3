import javax.swing.*;
import java.awt.*;

/**
 * Provides a box which represents whether an action was successful
 * or unsuccessful by changing colour to green or red respectively.
 *
 * @author Jonathan Beaumont
 */
public class ActionStatus extends JPanel {

    static final private Color SUCCESS_COLOR = Color.GREEN;
    static final private Color FAIL_COLOR = Color.RED;
    private static final long serialVersionUID = 2142623422463658797L;

    /**
     * Constructor. Sets the size of the box.
     *
     * @param size  The size of the box
     */
    public ActionStatus(int size) {
        this.setPreferredSize(new Dimension(size, size));
        this.setMaximumSize(new Dimension(size, size));
        this.setMinimumSize(new Dimension(size, size));
        this.setBackground(SUCCESS_COLOR);
    }
    public void actionSuccessful(boolean success) {
        if (success) {
            this.setBackground(SUCCESS_COLOR);
        } else {
            this.setBackground(FAIL_COLOR);
        }
    }
}