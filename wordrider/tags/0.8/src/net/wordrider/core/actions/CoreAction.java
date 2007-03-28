package net.wordrider.core.actions;

import net.wordrider.core.Lng;
import net.wordrider.core.MainApp;
import net.wordrider.core.managers.ManagerDirector;
import net.wordrider.utilities.Swinger;

import javax.swing.*;

/**
 * @author Vity
 */
abstract class CoreAction extends AbstractAction {
    protected CoreAction() {

    }

    public CoreAction(final String actionCode, final KeyStroke keyStroke, final String smallIcon) {
        super(actionCode);
        putValue(Action.NAME, Lng.getLabel(actionCode));
        putValue(Action.SHORT_DESCRIPTION, Lng.getHint(actionCode));
        putValue(Action.MNEMONIC_KEY, new Integer(Lng.getMnemonic(actionCode)));
        putValue(Action.ACCELERATOR_KEY, keyStroke);
        if (smallIcon != null)
            putValue(Action.SMALL_ICON, Swinger.getIcon(smallIcon));
    }

    public CoreAction(final String actionCode, final String name, final Integer mnemonic, final String smallIcon) {
        super(actionCode);
        putValue(Action.NAME, name);
        putValue(Action.SHORT_DESCRIPTION, name);
        putValue(Action.MNEMONIC_KEY, mnemonic);
        if (smallIcon != null)
            putValue(Action.SMALL_ICON, Swinger.getIcon(smallIcon));
    }

    // --Commented out by Inspection START (4.2.05 16:17):
    //    protected static void updateStatus() {
    //        MainApp.getInstance().getMainAppFrame().getManagerDirector().getToolbarManager().updateToolbar();
    //    }
    // --Commented out by Inspection STOP (4.2.05 16:17)

    static JFrame getMainFrame() {
        return MainApp.getInstance().getMainAppFrame();
    }

    static ManagerDirector getManagerDirector() {
        return MainApp.getInstance().getMainAppFrame().getManagerDirector();
    }
}
