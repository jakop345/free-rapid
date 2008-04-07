package cz.cvut.felk.erm.gui.actions;

import cz.cvut.felk.erm.core.MainApp;
import cz.cvut.felk.erm.gui.managers.ManagerDirector;
import cz.cvut.felk.erm.swing.Swinger;

import javax.swing.*;

/**
 * @author Vity
 */
abstract class CoreAction extends AbstractAction {
    protected CoreAction() {

    }

    public CoreAction(final String actionCode, final KeyStroke keyStroke, final String smallIcon) {
        super(actionCode);
        putValue(Action.NAME, actionCode);
        putValue(Action.ACCELERATOR_KEY, keyStroke);
        if (smallIcon != null)
            putValue(Action.SMALL_ICON, Swinger.getIconImage(smallIcon));
    }

    protected ManagerDirector getManagerDirector() {
        final MainApp instance = MainApp.getInstance(MainApp.class);
        final ManagerDirector managerDirector = instance.getManagerDirector();
        return managerDirector;
    }
}
