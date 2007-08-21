package cz.cvut.felk.timejuggler.swing;

import application.ResourceManager;
import application.ResourceMap;
import cz.cvut.felk.timejuggler.core.MainApp;
import cz.cvut.felk.timejuggler.gui.dialogs.ErrorDialog;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Pomocna trida pro caste volani nekterych metod. Nastavuje vychozi Look&Feel.
 * @author Vity
 */
public class Swinger {
    private static final Logger logger = Logger.getLogger(Swinger.class.getName());

    private Swinger() {
    }

    public static void showInformationDialog(final String message) {
        JOptionPane.showMessageDialog(Frame.getFrames()[0], message, getResourceMap().getString("informationMessage"), JOptionPane.INFORMATION_MESSAGE);
    }


    /**
     * Vrati obrazek podle key property v resourcu Nenajde-li se obrazek pod danym kodem, vypise WARNING pokud neni
     * obrazek nalezen
     * @param imagePropertyCode kod obrazku
     * @return obrazek
     */
    public static ImageIcon getIconImage(final String imagePropertyCode) {
        final ResourceMap map = getResourceMap();
        final ImageIcon imageIcon = map.getImageIcon(imagePropertyCode);
        if (imageIcon == null)
            logger.warning("Invalid image property code:" + imagePropertyCode);
        return imageIcon;
    }

    public static ResourceMap getResourceMap() {
        final ResourceManager rm = MainApp.getAContext().getResourceManager();
        return rm.getResourceMap();
    }

    public static ResourceMap getResourceMap(final Class className) {
        final ResourceManager rm = MainApp.getAContext().getResourceManager();
        return rm.getResourceMap(className);
    }

    public static Action getAction(Object actionName) {
        final Action action = MainApp.getAContext().getActionMap().get(actionName);
        if (action == null) {
            throw new IllegalStateException("Action with a name \"" + actionName + "\" does not exist.");
        }
        return action;
    }


    public static ActionMap getActionMap(Class aClass, Object actionsObject) {
        return MainApp.getAContext().getActionMap(aClass, actionsObject);
    }

    public static void showErrorMessage(ResourceMap map, final String message) {
        JOptionPane.showMessageDialog(Frame.getFrames()[0], message, map.getString("errorMessage"), JOptionPane.ERROR_MESSAGE);
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

    public static TableColumn updateColumn(JTable table, String name, final int columnId, final int width, TableCellRenderer renderer) {
        final TableColumnModel columnModel = table.getColumnModel();
        TableColumn column = columnModel.getColumn(columnId);
        if (renderer != null)
            column.setCellRenderer(renderer);
        column.setHeaderValue(name);
        column.setPreferredWidth(width);
        column.setWidth(width);
        column.setMinWidth(width);
        return column;
    }

    public static TableColumn updateColumn(JTable table, String name, final int columnId, final int width) {
        return updateColumn(table, name, columnId, width, null);
    }

    public static void showErrorDialog(Class clazz, final String messageResource, final Throwable e) {
        showErrorDialog(Swinger.getResourceMap(clazz), messageResource, e);
    }

    public static void showErrorDialog(final String messageResource, final Throwable e) {
        showErrorDialog(ErrorDialog.class, messageResource, e);
    }

    public static void showErrorDialog(ResourceMap map, final String messageResource, final Throwable e) {
        if (map == null)
            throw new IllegalArgumentException("Map cannot be null");
        final String localizedMessage = e.getLocalizedMessage();
        assert localizedMessage != null;
        final String text = map.getString(messageResource, localizedMessage);
        showErrorDialog(text, false, e);
    }

    public static void showErrorDialog(final String message, final boolean isMesssageResourceKey, final Throwable e) {
        final ResourceMap map = getResourceMap();
        final ErrorInfo errorInfo = new ErrorInfo(map.getString("errorMessage"), (isMesssageResourceKey) ? map.getString(message) : message, null, "EDT Thread", e, Level.SEVERE, null);
        JXErrorPane pane = new JXErrorPane();
        //  pane.setErrorReporter(new EmailErrorReporter());
        pane.setErrorInfo(errorInfo);
        JXErrorPane.showDialog(null, pane);
    }

}
