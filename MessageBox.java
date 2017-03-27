import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class MessageBox extends JTextPane {
    private static final long serialVersionUID = 3760312257620803477L;
    private StyledDocument doc;
    private Style style;
    public MessageBox() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(Color.white);
        this.doc = this.getStyledDocument();
        this.style = this.addStyle("My Style", null);
        this.setEditable(false);
        this.setEditorKit(new WrapEditorKit());
        this.setContentType("text/html");
    }
    public void addMessage(String message) {
        String fontFamily = this.getFont().getFamily();
        int fontSize = this.getFont().getSize();
        this.setText("<html><body style=\"font-family:" + fontFamily + ";font-size:" + fontSize + "\"><span style=\"color:black\">" + message + "\n</span><br>" + this.getText());
        /*
        StyleConstants.setForeground(style, Color.black);
        try {
            doc.insertString(0, message + "\n", style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }*/
        this.revalidate();

    }
    public void addMessage(String message, Color color) {
        String fontFamily = this.getFont().getFamily();
        int fontSize = this.getFont().getSize();
        this.setText("<html><body style=\"font-family:" + fontFamily + ";font-size:" + fontSize + "\"><span style=\"color:red\">" + message + "\n</span><br>" + this.getText());
        /*
        StyleConstants.setForeground(style, color);
        try {
            doc.insertString(0, "<html><b>" + message + "</b></html>\n", style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }*/
        this.revalidate();
    }

    class WrapEditorKit extends StyledEditorKit {
        private static final long serialVersionUID = 2002232136754917832L;
        ViewFactory defaultFactory=new WrapColumnFactory();
        public ViewFactory getViewFactory() {
            return defaultFactory;
        }

    }

    class WrapColumnFactory implements ViewFactory {
        public View create(Element elem) {
            String kind = elem.getName();
            if (kind != null) {
                if (kind.equals(AbstractDocument.ContentElementName)) {
                    return new WrapLabelView(elem);
                } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
                    return new ParagraphView(elem);
                } else if (kind.equals(AbstractDocument.SectionElementName)) {
                    return new BoxView(elem, View.Y_AXIS);
                } else if (kind.equals(StyleConstants.ComponentElementName)) {
                    return new ComponentView(elem);
                } else if (kind.equals(StyleConstants.IconElementName)) {
                    return new IconView(elem);
                }
            }

            // default to text display
            return new LabelView(elem);
        }
    }

    class WrapLabelView extends LabelView {
        public WrapLabelView(Element elem) {
            super(elem);
        }

        public float getMinimumSpan(int axis) {
            switch (axis) {
                case View.X_AXIS:
                    return 0;
                case View.Y_AXIS:
                    return super.getMinimumSpan(axis);
                default:
                    throw new IllegalArgumentException("Invalid axis: " + axis);
            }
        }

    }
}

