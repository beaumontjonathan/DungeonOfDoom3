import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Beaum on 13/03/2017.
 */
public class MapGrid extends JPanel {

    private static final long serialVersionUID = 5796210248951627500L;
    private int mapHeight;
    private int mapWidth;
    private JLabel[][] labelGrid;
    private char[][] charMap;
    final private int iconSize;

    private HashMap<Character, ImageIcon> MAP_ICONS;

    public MapGrid(int mapHeight, int mapWidth, int iconSize) {

        setupIcons(iconSize);

        this.iconSize = iconSize;
        this.mapHeight = mapHeight;
        this.mapWidth = mapWidth;
        this.labelGrid = new JLabel[mapHeight][mapWidth];
        this.charMap = new char[mapHeight][mapWidth];
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 0;


        JLabel label;
        for (int i = 0; i < mapHeight; i++) {
            for (int j = 0; j < mapWidth; j++) {
                label = new JLabel();
                label.setPreferredSize(new Dimension(iconSize, iconSize));
                labelGrid[i][j] = label;
                c.gridx = j;
                c.gridy = i;
                this.add(labelGrid[i][j], c);
            }
        }

    }

    private void setupIcons(final int iconSize) {
        MAP_ICONS = new HashMap<Character, ImageIcon>()
        {
            private static final long serialVersionUID = -6124191998911042975L;

            {
            put ('E', scaleImage("Icons/Map_icons/exit.png", "Exit", iconSize));
            put ('G', scaleImage("Icons/Map_icons/gold.png", "Gold", iconSize));
            put ('H', scaleImage("Icons/Map_icons/human.png", "Human player", iconSize));
            put ('B', scaleImage("Icons/Map_icons/bot.png", "Bot Player", iconSize));
            put ('.', scaleImage("Icons/Map_icons/blank.png", "Blank", iconSize));
            put ('#', scaleImage("Icons/Map_icons/wall.png", "Wall", iconSize));
            put ('X', scaleImage("Icons/Map_icons/nothing.png", "Nothing", iconSize));
            put (' ', scaleImage("Icons/Map_icons/empty.png", "Empty", iconSize));
        }};
    }

    public synchronized void insertCharMap(char[][] map) {
        if (map.length != mapHeight || map[0].length != mapWidth) {
            System.err.println("MAP SIZE INCORRECT");
        } else {
            for (int i = 0; i < mapHeight; i++) {
                insertMapRow(i, map[i]);
            }
        }
    }

    public synchronized void insertMapRow(int n, char[] row) {
        for(int i = 0; i < row.length; i++) {
            insertMapItem(i, n, row[i]);
        }
    }

    private synchronized void insertMapItem(int x, int y, char c) {
        if (MAP_ICONS.containsKey(c)) {
            labelGrid[y][x].setIcon(MAP_ICONS.get(c));
            charMap[y][x] = c;
        } else {
            System.err.println("Icon key not in MAP_ICONS");
        }
    }

    public synchronized void clearMap() {
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                insertMapItem(x, y, 'X');
            }
        }
    }

    private ImageIcon scaleImage(String filename, String description, int size) {
        ImageIcon imageIcon = new ImageIcon(new ImageIcon(filename, description).getImage().getScaledInstance(size, size, Image.SCALE_DEFAULT));
        return imageIcon;
    }
}
