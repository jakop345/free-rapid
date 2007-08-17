package cz.cvut.felk.timejuggler.gui;

import application.ApplicationContext;
import cz.cvut.felk.timejuggler.swing.components.ClockField;
import org.jdesktop.swingx.JXStatusBar;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Sprava a vytvoreni Statusbaru
 * @author Vity
 */
public class StatusBarManager implements PropertyChangeListener {
    private JXStatusBar statusbar;
    private JLabel infoLabel;
    private final MainPanelManager panelManager;


    public StatusBarManager(MainPanelManager panelManager, ApplicationContext context) {
        this.panelManager = panelManager;
        final Action action = context.getActionMap().get("showStatusBar");
        action.putValue(Action.SELECTED_KEY, true); //defaultni hodnota
        action.addPropertyChangeListener(new PropertyChangeListener() {
            //odchyt udalosti z akce pro zmenu viditelnosti statusbaru
            public void propertyChange(PropertyChangeEvent evt) {
                if (Action.SELECTED_KEY.equals(evt.getPropertyName())) {
                    setStatusBarVisible((Boolean) evt.getNewValue());
                }
            }
        });

    }

    public JXStatusBar getStatusBar() {
        if (statusbar == null) {
            statusbar = new JXStatusBar();

            statusbar.setName("statusbarPanel");
            infoLabel = new JLabel();
            infoLabel.setPreferredSize(new Dimension(300, 15));
            panelManager.getMenuManager().getMenuBar().addPropertyChangeListener("selectedText", this);
            statusbar.add(infoLabel, JXStatusBar.Constraint.ResizeBehavior.FIXED);
//            statusbar.add(progressBar, JXStatusBar.Constraint.ResizeBehavior.FIXED);
            statusbar.add(Box.createGlue(), JXStatusBar.Constraint.ResizeBehavior.FILL);
            final ClockField comp = new ClockField();
            //    progressBar.setVisible(false);
            comp.setHorizontalAlignment(JLabel.CENTER);
            statusbar.add(comp);
        }
        return statusbar;
    }

    private void setStatusBarVisible(boolean visible) {
        getStatusBar().setVisible(visible);
        //AppPrefs.storeProperty(AppPrefs.SHOW_STATUSBAR, visible); //ulozeni uzivatelskeho nastaveni
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if ("selectedText".equals(evt.getPropertyName())) {
            infoLabel.setText(evt.getNewValue().toString());
        }
    }
}
