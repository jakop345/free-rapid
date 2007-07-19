package cz.cvut.felk.timejuggler.gui;

import application.ApplicationContext;
import cz.cvut.felk.timejuggler.core.AppPrefs;
import org.jdesktop.swingx.JXStatusBar;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Sprava a vytvoreni Statusbaru
 * @author Vity
 */
public class StatusBarManager {
    private JXStatusBar statusbarPanel;


    public StatusBarManager(ApplicationContext context) {
        final Action action = context.getActionMap().get("showStatusBar");
        action.addPropertyChangeListener(new PropertyChangeListener() {
            //odchyt udalosti z akce pro zmenu viditelnosti statusbaru
            public void propertyChange(PropertyChangeEvent evt) {
                if (Action.SELECTED_KEY.equals(evt.getPropertyName())) {
                    setStatusBarVisible((Boolean) evt.getNewValue());
                }
            }
        });
        action.putValue(Action.SELECTED_KEY, AppPrefs.getProperty(AppPrefs.SHOW_STATUSBAR, true));
    }

    public JXStatusBar getStatusBar() {
        if (statusbarPanel == null) {
            statusbarPanel = new JXStatusBar();
            statusbarPanel.add(new JLabel("Tady je polozka statusbaru"));
        }
        return statusbarPanel;
    }

    private void setStatusBarVisible(boolean visible) {
        getStatusBar().setVisible(visible);
        AppPrefs.storeProperty(AppPrefs.SHOW_STATUSBAR, visible); //ulozeni uzivatelskeho nastaveni
    }
}
