import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class PlaceholderTextArea extends JTextArea {

    private static final long serialVersionUID = 6178066182355003267L;
    final private String placeholder;
    private boolean placeholderActive;
    final static private Color placeholderTextColor = Color.darkGray;
    final static private Color normalTextColor = Color.BLACK;
    private boolean editing;

    public PlaceholderTextArea(final String placeholder, int rows, int columns) {
        this.setRows(rows);
        this.setColumns(columns);

        this.placeholder = placeholder;
        this.placeholderActive = true;
        this.editing = false;

        this.setForeground(placeholderTextColor);
        this.setText(placeholder);

        this.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (placeholderActive) {
                    setText("");
                    setForeground(normalTextColor);
                    placeholderActive = false;
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (getText().equals("")) {
                    placeholderActive = true;
                    setForeground(placeholderTextColor);
                    setText(placeholder);
                } else {
                    placeholderActive = false;
                }
            }
        });
    }

    public String getMessage() {
        if (placeholderActive) {
            return "";
        } else {
            String text = getText();
            resetPlaceholder();
            return text;
        }
    }

    private void resetPlaceholder() {
        placeholderActive = true;
        setForeground(placeholderTextColor);
        setText(placeholder);
    }
}