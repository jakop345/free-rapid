package net.wordrider.core.actions;

import net.wordrider.core.AppPrefs;
import net.wordrider.core.managers.interfaces.IHidAble;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Vity
 */
public final class ToggleStatusBarAction extends CoreAction {
    private final static ToggleStatusBarAction instance = new ToggleStatusBarAction();
    private final static String CODE = "ToggleStatusBarAction";
    private JMenuItem menuItem;

    public static ToggleStatusBarAction getInstance(final JMenuItem menuItem) {
        instance.menuItem = menuItem;
        menuItem.setSelected(AppPrefs.getProperty(AppPrefs.SHOW_STATUSBAR, true));
        return instance;
    }

    private ToggleStatusBarAction() {
        super(CODE, KeyStroke.getKeyStroke("F12"), null);    //call to super
    }

    public final void actionPerformed(final ActionEvent e) {
        final IHidAble hidAble = getManagerDirector().getStatusbarManager();
        hidAble.setVisible(!hidAble.isVisible());
        menuItem.setSelected(hidAble.isVisible());
    }
}
