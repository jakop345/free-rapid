package net.wordrider.core.actions;

import net.wordrider.core.managers.AreaManager;
import net.wordrider.dialogs.AboutDialog;

import java.awt.event.ActionEvent;

/**
 * @author Vity
 */
public final class ShowAboutAction extends CoreAction {
    private final static ShowAboutAction instance = new ShowAboutAction();
    private final static String CODE = "ShowAboutAction";

    public static ShowAboutAction getInstance() {
        return instance;
    }

    private ShowAboutAction() {
        super(CODE, null, null);    //call to super
    }

    public final void actionPerformed(final ActionEvent e) {
        new AboutDialog(getMainFrame());
        AreaManager.getInstance().grabActiveFocus();
    }
}
