package net.wordrider.core.actions;

import net.wordrider.core.managers.AreaManager;
import net.wordrider.dialogs.settings.SettingsDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * @author Vity
 */
public final class ShowUserSettings extends CoreAction {
    private final static ShowUserSettings instance = new ShowUserSettings();
    private final static String CODE = "ShowUserSettingsAction";

    public static ShowUserSettings getInstance() {
        return instance;
    }

    private ShowUserSettings() {
        super(CODE, KeyStroke.getKeyStroke(KeyEvent.VK_F10, InputEvent.CTRL_MASK), "options.gif");    //call to super
    }

    public final void actionPerformed(final ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new SettingsDialog(getMainFrame());
                AreaManager.getInstance().grabActiveFocus();
            }
        });
    }

    public void setUpSendMethod() {
        new SettingsDialog(getMainFrame(), true);
    }
}
