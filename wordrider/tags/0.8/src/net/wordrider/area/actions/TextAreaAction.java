package net.wordrider.area.actions;

import net.wordrider.area.RiderArea;
import net.wordrider.core.Lng;
import net.wordrider.core.MainApp;
import net.wordrider.core.managers.AreaManager;
import net.wordrider.core.managers.interfaces.IFileInstance;
import net.wordrider.utilities.Swinger;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;
import java.awt.event.ActionEvent;

/**
 * @author Vity
 */
abstract class TextAreaAction extends TextAction {
    public TextAreaAction(final String actionCode, final KeyStroke keyStroke, final String smallIcon) {
        super(actionCode);
        putValue(Action.NAME, Lng.getLabel(actionCode));
        putValue(Action.SHORT_DESCRIPTION, Lng.getHint(actionCode));
        putValue(Action.MNEMONIC_KEY, new Integer(Lng.getMnemonic(actionCode)));
        putValue(Action.ACCELERATOR_KEY, keyStroke);
        if (smallIcon != null)
            putValue(Action.SMALL_ICON, Swinger.getIcon(smallIcon));
    }

    abstract public void actionPerformed(ActionEvent e);

    public final KeyStroke getKeyStroke() {
        return (KeyStroke) getValue(Action.ACCELERATOR_KEY);
    }

    static AreaManager getAreaManager() {
        return MainApp.getInstance().getMainAppFrame().getManagerDirector().getAreaManager();
    }


    final RiderArea getRiderArea(final ActionEvent e) {
        final JTextComponent c = getTextComponent(e);
        if (!(c instanceof RiderArea)) {
            final IFileInstance instance = getAreaManager().getActiveInstance();
            if (instance != null) {
                AreaManager.grabActiveFocus(instance);
                return (RiderArea) instance.getRiderArea();
            }
            return null;
        } else return (RiderArea) c;
    }

}
