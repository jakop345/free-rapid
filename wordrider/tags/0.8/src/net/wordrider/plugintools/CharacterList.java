package net.wordrider.plugintools;

import net.wordrider.area.RiderStyles;
import net.wordrider.core.AppPrefs;
import net.wordrider.core.Lng;
import net.wordrider.core.MainApp;
import net.wordrider.core.managers.AreaManager;
import net.wordrider.core.managers.interfaces.IFileInstance;
import net.wordrider.utilities.LogUtils;
import net.wordrider.utilities.Swinger;
import net.wordrider.utilities.Utils;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Stack;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public final class CharacterList extends PluginTool implements MouseMotionListener, MouseListener, ItemListener {
    private final static int zoomWindowWidth = 36;
    private JTable charsTable;


    private JTable lastUsedCharsTable;
    private JTextPane editor = null;

    private JComboBox charsTypeCombo;

    private static Cursor blankCursor;
    private static boolean nowZooming = false;

    private boolean firstTime = true;

    private JWindow zoomWindow;
    private BufferedImage zoomImage = null;
    private final static Logger logger = Logger.getLogger(CharacterList.class.getName());

    private Character activeCharacter;
    public static final char[] TI92PC_UNICODE_FONT = {'\u20AC', '\u0081', '\u201A', '\u0192', '\u201E', '\u2026', '\u2020', '\u2021', '\u02C6', '\u2030', '\u0160', '\u2039', '\u0152', '\u008D', '\u017D', '\u008F', '\u0090', '\u2018', '\u2019', '\u201C', '\u201D', '\u2013', '\u2014', '\u00C6', '\u02DC', '\u2122', '\u0161', '\u203A', '\u0153', '\u009D', '\u017E', '\u0178', '\u00A1', '\u00A2', '\u00A3', '\u00A4', '\u00A5', '\u00A6', '\u007C', '\u00A7', '\u00A7', '\u00A8', '\u00AA', '\u00D7', '\u00A9', '\u00AA', '\u00A8', '\u00AB', '\u00AC', '\u00AD', '\u00AE', '\u00AF', '\u00B0', '\u00B1', '\u00B6', '\u00B2', '\u00B3', '\u00B4', '\u00B5', '\u00DE', '\u00B6', '\u00B7', '\u00B8', '\u00B9', '\u00C0', '\u00C1', '\u00C2', '\u00C3', '\u00C4', '\u00C5', '\u00E6', '\u00C7', '\u00C8', '\u00C9', '\u00CA', '\u00CB', '\u00CD', '\u00CC', '\u00CD', '\u00CF', '\u00D0', '\u00D1', '\u00D2', '\u00D3', '\u00D4', '\u00D5', '\u00D6', '\u0078', '\u00D8', '\u00D9', '\u00DA', '\u00DB', '\u00DC', '\u00DD', '\u00FE', '\u00DF', '\u00E0', '\u00E1', '\u00E2', '\u00E3', '\u00E4', '\u00E5', '\u00E6', '\u00E7', '\u00E8', '\u00E9', '\u00EA', '\u00EB', '\u00EC', '\u00ED', '\u00EE', '\u00EF', '\u00F0', '\u00F1', '\u00F2', '\u00F3', '\u00F4', '\u00F5', '\u00F6', '\u00F7', '\u00F8', '\u00F9', '\u00FA', '\u00FB', '\u00FC', '\u00FD', '\u00FE', '\u00FF'};

    public CharacterList() {
        super();
        try {
            setFileInstance(null);
            init();
        } catch (Exception e) {
            LogUtils.processException(logger, e);
        }
    }


    public final String getName() {
        return Lng.getLabel("characters.title");
    }

    public String getTabName() {
        return getName();
    }

    public final Icon getIcon() {
        return Swinger.getIcon("char.gif");
    }

    public final String getTip() {
        return Lng.getLabel("characters.tooltip");
    }

    public final void activate() {
        setFileInstance(AreaManager.getInstance().getActiveInstance());
        logger.info("CharsList activated");
    }

    public final void deactivate() {
        setFileInstance(null);
        logger.info("CharsList deactivated");
    }

    private void setEditor(final JTextComponent pane) {
        getContentPane().setEnabled((this.editor = (JTextPane) pane) != null);
    }


    public void setFileInstance(final IFileInstance instance) {
        if (instance != null)
            setEditor(instance.getRiderArea());
        else
            setEditor(null);        
    }

    public void updateData() {
    }

    private CharsTableModel getCharsTableModel() {
        return ((CharsTableModel) charsTable.getModel());
    }

    private void initTable(final JTable table, final KeyListener keyListener, final TableCellRenderer renderer) {
        table.setDefaultRenderer(Object.class, renderer);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setColumnSelectionAllowed(false);
        //        charsTable.getSelectionModel().addListSelectionListener(this);
        table.setRowSelectionAllowed(false);
        //charsTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
        table.setTableHeader(null);
        table.addMouseMotionListener(this);
        table.setFont(RiderStyles.getAreaFont().deriveFont(Font.PLAIN, 13));
        table.addMouseListener(this);
        table.setRowHeight(25);
        table.addKeyListener(keyListener);
        table.setFocusCycleRoot(true);
    }

    private static Stack<Character> getLastUsedCharsFromPreferences() {
        final Stack<Character> result = new Stack<Character>();
        if (AppPrefs.getProperty(AppPrefs.USED_CHARS_SAVE, true)) {
            final String characters = AppPrefs.getProperty(AppPrefs.USED_CHARS, "");
            final int charactersLength = characters.length();
            for (int i = 0; i < charactersLength; ++i) {
                result.add(characters.charAt(i));
            }
        }
        return result;

    }

    private void storeLastUsedChars() {
        if (AppPrefs.getProperty(AppPrefs.USED_CHARS_SAVE, true)) {
            final Stack<Character> result = getUsedCharsTableModel().getValues();
            final StringBuilder characters = new StringBuilder(result.size());
            Character ch;
            for (Character aResult : result) {
                ch = aResult;
                if (ch != null)
                    characters.append(ch.charValue());
            }
            AppPrefs.storeProperty(AppPrefs.USED_CHARS, characters.toString());
            AppPrefs.storeProperty(AppPrefs.USED_CHARS_SAVE, true);
        } else {
            AppPrefs.removeProperty(AppPrefs.USED_CHARS);
        }
    }

    private void init() {
        final Toolkit tk = Toolkit.getDefaultToolkit();
        final byte bogus[] = {(byte) 0};
        blankCursor = tk.createCustomCursor(tk.createImage(bogus), new Point(0, 0), "");

        zoomWindow = new JWindow(MainApp.getInstance().getMainAppFrame()) {
            public void paint(final Graphics g) {
                g.drawImage(zoomImage, 0, 0, zoomWindow);
            }
        };
        zoomWindow.setCursor(blankCursor);
        zoomWindow.pack();

        final CellRenderer renderer = new CellRenderer();
        final KeyListener keyListener = new AddCharByKeyListener();
        charsTable = new JTable(new CharsTableModel());
        initTable(charsTable, keyListener, renderer);


        lastUsedCharsTable = new JTable(new LastUsedCharsTableModel(getLastUsedCharsFromPreferences()));
        initTable(lastUsedCharsTable, keyListener, renderer);
        lastUsedCharsTable.setBorder(BorderFactory.createEtchedBorder());//createLoweredBevelBorder());

        final Container contentPane = getContentPane();

        contentPane.setLayout(new BorderLayout());

        charsTypeCombo = new JComboBox();
        // charsTypeCombo.setMinimumSize(new Dimension(100, 25));
        charsTypeCombo.setPreferredSize(new Dimension(170, 23));
        final ArrayList<String> list = getCharsTableModel().getAvailableCharTables();
        for (String aList : list) charsTypeCombo.addItem(aList);
        charsTypeCombo.addItemListener(this);
        itemStateChanged(null);


        final JPanel container = new JPanel(new BorderLayout());
        final JPanel jPanel1 = new JPanel(new BorderLayout());
        final JPanel jPanel2 = new JPanel(new BorderLayout());

        //      jPanel1.setMinimumSize(new Dimension(100, 30));
        jPanel1.setPreferredSize(new Dimension(150, 30));
        //jPanel2.setMaximumSize(new Dimension(-1,25));
        container.add(jPanel2, BorderLayout.NORTH);
        container.add(jPanel1, BorderLayout.SOUTH);

        jPanel2.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 4));
        jPanel2.add(lastUsedCharsTable, BorderLayout.CENTER);
        jPanel2.setPreferredSize(new Dimension(150, 35));
        jPanel1.setBorder(BorderFactory.createEmptyBorder(0, 2, 4, 5));
        jPanel1.add(charsTypeCombo, BorderLayout.WEST);

        final JScrollPane pane = new JScrollPane(charsTable);
        pane.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        //container.setBorder(BorderFactory.createEtchedBorder());

        contentPane.add(container, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(charsTable), BorderLayout.CENTER);

        JComponent parentComponent = getComponent();
        parentComponent.setPreferredSize(new Dimension(180, 250));
        parentComponent.setMaximumSize(new Dimension(200, 250));
        //        this.setMinimumSize(this.getPreferredSize());
        parentComponent.setVisible(true);
    }


    private boolean firstTime() {
        return firstTime;
    }

    private void refresh() {
        firstTime = false;
        getContentPane().repaint();
    }


    /// Shows (updates) the character zoom window
    private void showZoomed(final JTable table, final int selRow, final int selColumn, final Character selCharacter) {
        if (selCharacter == null) return;
        activeCharacter = selCharacter;
        table.setCursor(blankCursor);

        if (!nowZooming) {
            if (zoomWindow.getWarningString() != null)
                /// If this is not opened as a "secure" window,
                /// it has a banner below the zoom dialog which makes it look really BAD
                /// So enlarge it by a bit
                zoomWindow.setSize(zoomWindowWidth, zoomWindowWidth);
            else
                zoomWindow.setSize(zoomWindowWidth + 1, zoomWindowWidth + 1);
        }

        /// Prepare zoomed image
        zoomImage =
                (BufferedImage) zoomWindow.createImage(zoomWindowWidth + 1,
                        zoomWindowWidth + 1);
        final Graphics2D g2 = (Graphics2D) zoomImage.getGraphics();
        final Font testFont = RiderStyles.getAreaBigFont();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.white);
        g2.fillRect(0, 0, zoomWindowWidth, zoomWindowWidth);
        g2.setColor(Color.black);
        g2.drawRect(0, 0, zoomWindowWidth, zoomWindowWidth);
        g2.setFont(testFont);
        final FontMetrics metrics = table.getFontMetrics(testFont);
        g2.drawString(selCharacter.toString(), (zoomWindowWidth - metrics.charWidth(selCharacter.charValue())) / 2, (zoomWindowWidth - metrics.getHeight()) / 2 + metrics.getAscent());
        g2.dispose();
        /// This is sort of redundant... since there is a paint function
        /// inside zoomWindow definition that does the drawImage.
        /// (I should be able to call just repaint() here)
        /// However, for some reason, that paint function fails to respond
        /// from second time and on; So I have to force the paint here...
        //if (nowZooming) zoomWindow.hide();
        zoomWindow.getGraphics().drawImage(zoomImage, 0, 0, getContentPane());
        final Rectangle r = table.getCellRect(selRow, selColumn, false);

        //GlyphVector gv;
        final Point canvasLoc = table.getLocationOnScreen();

        /// Calculate the zoom net.wordrider.area's location and size...
        /// Position and set size of zoom window as needed
        final int relDialogX = r.x - (zoomWindowWidth - r.width) / 2;
        final int relDialogY = r.y - (zoomWindowWidth - r.height) / 2;
        zoomWindow.setLocation(canvasLoc.x + relDialogX, canvasLoc.y + relDialogY);
        if (!nowZooming)
            zoomWindow.setVisible(true);
        nowZooming = true;
        //prevZoomChar = currMouseOverChar;

        // Windows does not repaint correctly, after
        // a zoom. Thus, we need to force the canvas
        // to repaint, but only once. After the first repaint,
        // everything stabilizes. [ABP]
        if (firstTime())
            refresh();
    }

    private static Character getSelectedCharacter(final JTable table, final int selRow, final int selColumn) {
        return (Character) table.getModel().getValueAt(selRow, selColumn);
    }

    private static Character getSelectedCharacter(final JTable table) {
        return (Character) table.getModel().getValueAt(table.getSelectedRow(), table.getSelectedColumn());
    }

    public final void mouseDragged(final MouseEvent e) {
        if (nowZooming) {
            final JTable table = (JTable) e.getSource();
            final int selRow = table.rowAtPoint(e.getPoint());
            final int selColumn = table.columnAtPoint(e.getPoint());

            final Character selCh = getSelectedCharacter(table, selRow, selColumn);
            if ((selCh) != null && selCh.compareTo(activeCharacter) != 0)
                showZoomed(table, selRow, selColumn, selCh);
        }
    }

    public final void mouseMoved(final MouseEvent e) {
        //implement - call to super class
    }

    public final void mouseClicked(final MouseEvent e) {
        //   mouseDragged(e);
    }

    //    private final JTextPane getEditor() {
    //        return editor;
    //    }

    public final void mouseEntered(final MouseEvent e) {
    }

    public final void mouseExited(final MouseEvent e) {
    }

    public final void mousePressed(final MouseEvent e) {
        final JTable table = (JTable) e.getSource();
        final int selRow = table.rowAtPoint(e.getPoint());
        final int selColumn = table.columnAtPoint(e.getPoint());
        final Character selCh = getSelectedCharacter(table, selRow, selColumn);
        showZoomed(table, selRow, selColumn, selCh);
    }

    public final void mouseReleased(final MouseEvent e) {
        if (nowZooming)
            zoomWindow.setVisible(false);
        nowZooming = false;
        final JTable table = (JTable) e.getSource();
        final Character ch = getSelectedCharacter(table, table.rowAtPoint(e.getPoint()), table.columnAtPoint(e.getPoint()));
        if (ch != null) {
            insertCharacter(ch);
            if (table.equals(charsTable))
                getUsedCharsTableModel().addUsedChar(ch);
        }
        e.consume();
        table.setCursor(Cursor.getDefaultCursor());
    }

    private LastUsedCharsTableModel getUsedCharsTableModel() {
        return (LastUsedCharsTableModel) lastUsedCharsTable.getModel();
    }

    /**
     * When in combobox changes the selection
     */
    public final void itemStateChanged(final ItemEvent e) {
        getCharsTableModel().selectCharTable(charsTypeCombo.getSelectedIndex());
    }

    private static final class CellRenderer extends DefaultTableCellRenderer {
        private static final boolean isWindows = Utils.isWindows();

        /**
         * Creates a default table cell renderer.
         */
        public CellRenderer() {
            super();    //call to super
            this.setHorizontalAlignment(CENTER);
            this.setHorizontalTextPosition(CENTER);
        }

        public final Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
            if (value != null) {
                final int charValue = (Character) value;
                final String hotKey = (charValue < 255 && isWindows) ? "alt-0" + charValue + "  " : "";

                this.setToolTipText(hotKey + Lng.getLabel("characters.unicode", Integer.toHexString(charValue)));
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    private void insertCharacter(final Character ch) {
        if (editor == null)
            return;
        //        try {
        editor.replaceSelection(ch.toString());
        //editor.select(editor.getCaretPosition(), editor.getCaretPosition());
        //editor.getDocument().insertString(editor.getCaretPosition(), ch.toString(), editor.getInputAttributes());
        Swinger.inputFocus(editor);
        //        } catch (BadLocationException ex) {
        //            LogUtils.processException(logger, ex);
        //            return;
        //        }

    }

    private final class AddCharByKeyListener implements KeyListener {
        /**
         * Invoked when a LASTOPENFOLDER_KEY has been pressed. See the class description for {@link
         * java.awt.event.KeyEvent} for a definition of a LASTOPENFOLDER_KEY pressed event.
         */
        public void keyPressed(final KeyEvent e) {
            //implement - call to super class
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                final JTable table = (JTable) e.getSource();
                final Character ch = getSelectedCharacter(table);
                if (ch != null) {
                    insertCharacter(ch);
                    if (table.equals(charsTable))
                        ((LastUsedCharsTableModel) lastUsedCharsTable.getModel()).addUsedChar(ch);
                }
                e.consume();
            }

        }

        public void keyReleased(final KeyEvent e) {
            //implement - call to super class
        }

        public void keyTyped(final KeyEvent e) {
            //implement - call to super class
        }

    }


    private final static class CharsTableModel extends AbstractTableModel {
        private static final char[] GREEK_CHARS = {'\u20ac', '\u0081', '\u201a', '\u0192', '\u201e', '\u2026', '\u2020', '\u2021', '\u02c6', '\u2030', '\u00b1', '\u0160', '\u0152', '\u008d', '\u008f', '\u0090', '\u2018', '\u2019', '\u201c', '\u201d', '\u00b7', '\u00b6', '\u017d', '\u2039'};
        private static final char[] MATH_SYMBOLS1 = {'\u00b4', '\u00ae', '\u00af', '\u00b0', '\u00b8', '\u00a7', '\u2014', '\u00c6', '\u0152', '\u00AA', '\u00bf', '\u00bd', '\u00bc', '\u00be', '\u00a9', '\u0153', '\u017E', '\u009D', '\u0161', '\u203a', '\u0178', '\u00a2', '\u02dc', '\u00ac', '\u00b3', '\u00d7', '\u00de', '\u00BB'};
        private static final char[] MATH_SYMBOLS2 = {'\u00c6', '\u00b8', '\u007c', '\u0022', '\u003b', '\u003a', '\u02c6', '\\', '\u2013', '\u003c', '\u003e', '\u007b', '\u007d', '\u005b', '\u005d', '\u0028', '\u0029', '\u002c', '\u005e', '\u0152'};
        private static final char[] SPECIAL_SYMBOLS = {'\u0021', '\u0022', '\u0023', '\u0024', '\u0025', '\u0026', '\'', '\u003f', '\u0040', '\u005f', '\u00a1', '\u00a2', '\u00a3', '\u00a4', '\u00a5', '\u00a6', '\u00a8', '\u00ac', '\u00b9', '\u00d7', '\u00DE'};
        private final static int columnsCount = 5;
        private int dataLength = 0;
        private char[] charArray;

        /**
         * Returns the number of columns in this data table.
         * @return the number of columns in the model
         */
        public final int getColumnCount() {
            return columnsCount;
        }

        /**
         * Returns the number of rows in this data table.
         * @return the number of rows in the model
         */
        public final int getRowCount() {
            return (dataLength / columnsCount) + ((dataLength % columnsCount != 0) ? 1 : 0);    //call to super
        }

        public final boolean isCellEditable(final int row, final int column) {
            return false;
        }

        public final Object getValueAt(int row, final int column) {
            row = row * columnsCount + column;
            return (row < dataLength && row >= 0) ? charArray[row] : null;
        }

        public final ArrayList<String> getAvailableCharTables() {
            final ArrayList<String> list = new ArrayList<String>(5);
            list.add(Lng.getLabel("characters.list.greek"));
            list.add(Lng.getLabel("characters.list.math1"));
            list.add(Lng.getLabel("characters.list.math2"));
            list.add(Lng.getLabel("characters.list.specialSyms"));
            list.add(Lng.getLabel("characters.list.allChars"));
            return list;
        }

        private void setCharArray(final char[] array) {
            this.charArray = array;
            dataLength = array.length;
            fireTableDataChanged();
        }

        public final void selectCharTable(final int selectedIndex) {
            switch (selectedIndex) {
                case 0:
                    setCharArray(GREEK_CHARS);
                    break;
                case 1:
                    setCharArray(MATH_SYMBOLS1);
                    break;
                case 2:
                    setCharArray(MATH_SYMBOLS2);
                    break;
                case 3:
                    setCharArray(SPECIAL_SYMBOLS);
                    break;
                default:
                    setCharArray(TI92PC_UNICODE_FONT);
            }
        }
    }

    private final static class LastUsedCharsTableModel extends AbstractTableModel {
        private final static int COLUMNS_COUNT = 8;
        private final Stack<Character> usedCharsList;

        public LastUsedCharsTableModel(final Stack<Character> lastUsedCharacters) {
            super();    //call to super
            this.usedCharsList = lastUsedCharacters;
            usedCharsList.setSize(COLUMNS_COUNT);
        }

        public final Stack<Character> getValues() {
            return usedCharsList;
        }

        public final void addUsedChar(final Character ch) {
            if (!usedCharsList.contains(ch)) {
                usedCharsList.add(0, ch);
                usedCharsList.setSize(COLUMNS_COUNT);
                fireTableDataChanged();
            }
        }


        /**
         * Returns the number of columns in this data table.
         * @return the number of columns in the model
         */
        public final int getColumnCount() {
            return COLUMNS_COUNT;
        }

        /**
         * Returns the number of rows in this data table.
         * @return the number of rows in the model
         */
        public final int getRowCount() {
            return 1;    //call to super
        }

        public final boolean isCellEditable(final int row, final int column) {
            return false;
        }

        public final Object getValueAt(final int row, final int column) {
            return (column >= 0 && column < usedCharsList.size()) ? usedCharsList.get(column) : null;
        }
    }

    public boolean closeSoft() {
        storeLastUsedChars();
        return true;
    }

    public void closeHard() {
        //implement - call to super class
    }

}