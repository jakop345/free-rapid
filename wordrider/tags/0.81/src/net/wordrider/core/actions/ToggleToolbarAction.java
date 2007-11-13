package net.wordrider.core.actions;

import net.wordrider.core.AppPrefs;
import net.wordrider.core.managers.interfaces.IHidAble;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Vity
 */
public final class ToggleToolbarAction extends CoreAction {
    private final static ToggleToolbarAction instance = new ToggleToolbarAction();
    private final static String CODE = "ToggleToolbarAction";
    private JMenuItem menuItem;

    public static ToggleToolbarAction getInstance(final JMenuItem menuItem) {
        instance.menuItem = menuItem;
        menuItem.setSelected(AppPrefs.getProperty("net.wordrider.gui.showToolbar", true));
        return instance;
    }

    private ToggleToolbarAction() {
        super(CODE, KeyStroke.getKeyStroke("F11"), null);    //call to super
    }

    public final void actionPerformed(final ActionEvent e) {
        final IHidAble hidAble = getManagerDirector().getToolbarManager();
        hidAble.setVisible(!hidAble.isVisible());
        menuItem.setSelected(hidAble.isVisible());
    }
}
