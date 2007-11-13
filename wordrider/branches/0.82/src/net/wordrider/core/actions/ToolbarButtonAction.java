package net.wordrider.core.actions;

import net.wordrider.dialogs.AboutDialog;

import java.awt.event.ActionEvent;

/**
 * @author Vity
 */
final class ToolbarButtonAction extends CoreAction {
    private final static String CODE = "ToolbarButtonAction";

    private ToolbarButtonAction() {
        super(CODE, null, 0, null);    //call to super
    }

    public final void actionPerformed(final ActionEvent e) {
        new AboutDialog(getMainFrame());
    }
}
