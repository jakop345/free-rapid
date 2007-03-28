package net.wordrider.utilities;

import net.wordrider.core.AppPrefs;
import net.wordrider.core.Lng;
import net.wordrider.core.swing.GraphicMenuItem;
import net.wordrider.core.swing.RecentsComboModel;
import net.wordrider.files.InvalidDataTypeException;
import net.wordrider.files.NotSupportedFileException;
import net.wordrider.files.ti68kformat.TIImageDecoder;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Iterator;
import java.util.Stack;
import java.util.logging.Logger;

//import java.util.HashMap;

/**
 * @author Vity
 */
public final class Swinger {
    // --Commented out by Inspection (26.2.05 17:40): public static int RESULT_CANCEL = 2;
    public static final int RESULT_NO = 1;
    public static final int RESULT_YES = 0;

    private static final String MESSAGE_ERROR_TITLE_CODE = "message.error.title";
    private static final String MESSAGE_CONFIRM_TITLE_CODE = "message.confirm.title";
    private static final String MESSAGE_INFORMATION_TITLE_CODE = "message.information.title";
    private static final String MESSAGE_WARNING_TITLE_CODE = "message.warning.title";
    private static final String MESSAGE_BTN_YES_CODE = "message.button.yes";
    private static final String MESSAGE_BTN_NO_CODE = "message.button.no";
    public static final String MESSAGE_BTN_CANCEL_CODE = "message.button.cancel";

    private static final double FACTOR = 0.92;
    //    private static HashMap cachedResources = new HashMap(2);
    public static boolean antialiasing = AppPrefs.getProperty(AppPrefs.ANTIALIASING, false);
    private final static Logger logger = Logger.getLogger(Swinger.class.getName());

    private Swinger() {
    }

    public static JRadioButton getRadio(final String code) {
        final JRadioButton radio = new JRadioButton(Lng.getLabel(code));
        radio.setMnemonic(Lng.getMnemonic(code));
        return radio;
    }

    public static JLabel getLabel(final String code) {
        final JLabel label = new JLabel(Lng.getLabel(code));
        label.setDisplayedMnemonic(Lng.getMnemonic(code));
        return label;
    }

    public static JLabel getLabel(final String code, final String icon) {
        final JLabel label = getLabel(code);
        label.setIcon(getIcon(icon));
        return label;
    }


    public static JCheckBox getCheckBox(final String code) {
        final JCheckBox checkbox = new JCheckBox(Lng.getLabel(code));
        checkbox.setMnemonic(Lng.getMnemonic(code));
        return checkbox;
    }

    public static JCheckBox getCheckBox(final String code, final String propertyName, final boolean defaultValue) {
        final JCheckBox checkbox = getCheckBox(code);
        checkbox.setSelected(AppPrefs.getProperty(propertyName, defaultValue));
        return checkbox;
    }

    public static JButton getButton(final String code, final Action action) {
        action.putValue(Action.NAME, Lng.getLabel(code));
        action.putValue(Action.ACTION_COMMAND_KEY, code);
        action.putValue(Action.MNEMONIC_KEY, (int) Lng.getMnemonic(code));
        return new JButton(action);
    }

    public static JButton getButton(final String code) {
        final JButton button = new JButton(Lng.getLabel(code));
        button.setMnemonic(Lng.getMnemonic(code));
        return button;
    }

    public static JToggleButton getToggleButton(final String code, final boolean selected) {
        final JToggleButton button = new JToggleButton(Lng.getLabel(code));
        button.setMnemonic(Lng.getMnemonic(code));
        button.setSelected(selected);
        return button;
    }

//    public static String getAcceleratorText(KeyStroke accelerator) {
//        if (acceleratorDelimiter == null) {
//            final String acc = UIManager.getString("MenuItem.acceleratorDelimiter");
//            if (acc == null)
//                acceleratorDelimiter = "+";
//            else acceleratorDelimiter = acc;
//        }
//
//        String acceleratorText = "";
//        if (accelerator != null) {
//            int modifiers = accelerator.getModifiers();
//            if (modifiers > 0) {
//                acceleratorText = KeyEvent.getKeyModifiersText(modifiers);
//                acceleratorText += acceleratorDelimiter;
//            }
//
//            final int keyCode = accelerator.getKeyCode();
//            if (keyCode != 0)
//                acceleratorText += KeyEvent.getKeyText(keyCode);
//            else
//                acceleratorText += accelerator.getKeyChar();
//        }
//        return acceleratorText;
//    }

    //    public static ImageIcon getCachedIcon(final String fileName) {
    //        if (cachedResources.containsKey(fileName)) {
    //            return (ImageIcon) cachedResources.get(fileName);
    //        } else {
    //            ImageIcon icon = getIcon(fileName);
    //            if (icon != null)
    //                cachedResources.put(fileName, icon);
    //            return icon;
    //        }
    //    }

    public static Color getColor(final String key, final Color defaultColor) {
        final String value = AppPrefs.getProperty(key);
        if (value != null) {
            return Color.decode(value);
        } else return defaultColor;
    }

    public static void setAntialiasing(final boolean value) {
        AppPrefs.storeProperty(AppPrefs.ANTIALIASING, value);
        antialiasing = value;
    }

    public static void setColor(final String key, final Color color) {
        AppPrefs.storeProperty(key, String.valueOf(color.getRGB()));
    }

    public static ImageIcon getIcon(final String fileName) {
        if (fileName == null)
            return null;
        try {
            return new ImageIcon(ImageIO.read(((URLClassLoader) Swinger.class.getClassLoader()).findResource(Consts.IMAGESDIR + fileName)));
        } catch (final IOException e) {
            logger.severe("Cannot load image " + fileName + "\nReason:" + e.getMessage());
            return null;
        }
    }

    public static Image getIconImage(final String fileName) {
        if (fileName == null)
            return null;
        try {
            return (ImageIO.read(((URLClassLoader) Swinger.class.getClassLoader()).findResource(Consts.IMAGESDIR + fileName)));
        } catch (final IOException e) {
            logger.severe("Cannot load image " + fileName + "\nReason:" + e.getMessage());
            return null;
        }
    }

    public static Image loadPicture(final File f) throws IOException, InvalidDataTypeException, NotSupportedFileException {
        final String extension = Utils.getExtension(f);
        if (extension.equals("jpg") || extension.equals("jpeg") || extension.equals("bmp") || extension.equals("png") || extension.equals("gif") || (extension.equals("wbmp"))) {
            final Image origImage;
            origImage = ImageIO.read(f);
            return origImage;
        }
//        if (extension.equals("bmp")) {
//            return new BMPReader(f).getImage();
//        }
        if (isImageExtension(extension)) {
            final TIImageDecoder ti = new TIImageDecoder();
            if (!ti.openFromFile(f))
                logger.warning("Image was loaded but the file might be corrupted (invalid checksum)");
            return Toolkit.getDefaultToolkit().createImage(ti);
        }
        return null;
    }

    public static boolean isImageExtension(String extension) {
        return extension.equals("89i") || extension.equals("92i") || extension.equals("9xi");
    }


    public static void addKeyActions(final JComponent component) {
        final InputMap map = component.getInputMap();
        map.put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, InputEvent.CTRL_MASK), new DefaultEditorKit.CopyAction());
        map.put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, InputEvent.SHIFT_MASK), new DefaultEditorKit.PasteAction());
        map.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.SHIFT_MASK), new DefaultEditorKit.CutAction());
        //map.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.SHIFT_MASK), new net.wordrider.area.RiderArea.TestAction(""));
        component.setInputMap(JComponent.WHEN_FOCUSED, map);
    }

    public static JComponent getGraphicItem(String title, String iconName, Action action) {
        return new GraphicMenuItem(Lng.getLabel(title + ".head"), Lng.getLabel(title + ".comment"), getIcon(iconName), action);
    }


    public static final class SelectAllOnFocusListener implements FocusListener {
        public final void focusGained(final FocusEvent e) {
            if (!e.isTemporary()) {
                //final Component component = ;
                ((JTextComponent) e.getComponent()).selectAll();
            }
        }

        public final void focusLost(final FocusEvent e) {
        }
    }

    public static void centerDialog(final Frame frame, final Component component) {
        final Dimension dlgSize = component.getPreferredSize();
        final Dimension frmSize = frame.getSize();
        final Point loc = frame.getLocation();
        component.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x,
                (frmSize.height - dlgSize.height) / 2 + loc.y);
    }

    public static void inputFocus(final JComboBox combo) {
        inputFocus((JComponent) combo.getEditor().getEditorComponent());
    }

    public static void inputFocus(final JComponent field) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                field.grabFocus();
                field.requestFocus();
            }
        });
    }

    public static void showErrorDialog(final Frame frame, final String message) {
        JOptionPane.showMessageDialog(frame, message, Lng.getLabel(Swinger.MESSAGE_ERROR_TITLE_CODE), JOptionPane.ERROR_MESSAGE);
    }

    public static void showWarningDialog(final Frame frame, final String message) {
        JOptionPane.showMessageDialog(frame, message, Lng.getLabel(Swinger.MESSAGE_WARNING_TITLE_CODE), JOptionPane.WARNING_MESSAGE);
    }

    public static void showInformationDialog(final Frame frame, final String message) {
        JOptionPane.showMessageDialog(frame, message, Lng.getLabel(Swinger.MESSAGE_INFORMATION_TITLE_CODE), JOptionPane.INFORMATION_MESSAGE);
    }


    public static int getChoice(final Frame frame, final String message) {

        return JOptionPane.showOptionDialog(frame, message, Lng.getLabel(MESSAGE_CONFIRM_TITLE_CODE),
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, new String[]{Lng.getLabel(MESSAGE_BTN_YES_CODE), Lng.getLabel(MESSAGE_BTN_NO_CODE)},
                null);
    }

    public static int getChoiceCancel(final Frame frame, final String message) {
        return JOptionPane.showOptionDialog(frame, message, Lng.getLabel(MESSAGE_CONFIRM_TITLE_CODE),
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, new String[]{Lng.getLabel(MESSAGE_BTN_YES_CODE), Lng.getLabel(MESSAGE_BTN_NO_CODE),
                Lng.getLabel(MESSAGE_BTN_CANCEL_CODE)},
                null);
    }

    public static int getButtonsOption(final Frame frame, final String[] buttonsCodes, final String message) {
        return JOptionPane.showOptionDialog(frame, message, Lng.getLabel(MESSAGE_CONFIRM_TITLE_CODE),
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, buttonsCodes,
                null);
    }

    public static Color brighter(final Color color) {
        if (color == null) {
            final Color c = new JPanel().getBackground();
            if (c != null)
                return c;
            else return UIManager.getDefaults().getColor("window");
        }
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        final int i = (int) (1.0 / (1.0 - FACTOR));
        if (r == 0 && g == 0 && b == 0) {
            return new Color(i, i, i);
        }
        if (r > 0 && r < i) r = i;
        if (g > 0 && g < i) g = i;
        if (b > 0 && b < i) b = i;
        final Color result = new Color(Math.min((int) (r / FACTOR), 255),
                Math.min((int) (g / FACTOR), 255),
                Math.min((int) (b / FACTOR), 255));
        if (result.equals(Color.WHITE)) {
            Color c = new JPanel().getBackground();
            if (c == null)
                return color;
            else return c;
        } else return result;
    }

    public static JComponent getTitleComponent(final String title) {
        return getTitleComponent(new JLabel(title));
    }

    public static JComponent getTitleComponent2(final String titleCode) {
        final JLabel label = new JLabel(Lng.getLabel(titleCode));
        label.setFont(label.getFont().deriveFont(Font.BOLD, 16));
        return getTitleComponent(label);
    }

    private static JComponent getTitleComponent(JLabel label) {
        JToolBar toolBar = new JToolBar();
        toolBar.setLayout(new GridBagLayout());
        toolBar.setFloatable(false);
        toolBar.setBorderPainted(false);
        toolBar.setOpaque(false);
        toolBar.add(label, new GridBagConstraints(0, 0, 1, 2, 0, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 4, 0, 2), 0, 0));
        toolBar.add(new JSeparator(SwingConstants.HORIZONTAL), new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 0, 0, 0), 0, 0));
//        toolBar.add(new JSeparator(SwingConstants.HORIZONTAL), new GridBagConstraints(1, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        return toolBar;
    }

    public static ComboBoxModel loadSearchUsedList(final String keyProperties) {
        String searched, key;
        final Stack<String> collection = new Stack<String>();
        for (int counter = 0; (searched = AppPrefs.getProperty(key = keyProperties + counter, null)) != null; ++counter)
        {
            if (searched.length() > 0) {
                collection.add(0, searched);
                AppPrefs.removeProperty(key);
            }
        }
        return new RecentsComboModel(collection);
    }

    public static void storeProperties(final String keyProperties, final JComboBox combo) {
        final Collection collection = ((RecentsComboModel) combo.getModel()).getList();
        int counter = collection.size() - 1;
        for (final Iterator it = collection.iterator(); it.hasNext(); --counter) {
            AppPrefs.storeProperty(keyProperties + counter, it.next().toString());
        }
    }

    public static boolean isEmpty(final JComboBox field) {
        final Object item = field.getEditor().getItem();
        return item == null || item.equals("");
    }


}
