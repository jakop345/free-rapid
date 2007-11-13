package net.wordrider.core.actions;

import net.wordrider.area.RiderArea;
import net.wordrider.core.managers.AreaManager;
import net.wordrider.core.managers.interfaces.IFileInstance;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Vity
 */
class ToggleViewAreaAction extends CoreAction {

    int borderType = -1;

    public ToggleViewAreaAction(final String actionCode, final KeyStroke keyStroke, final String smallIcon) {
        super(actionCode, keyStroke, smallIcon);
    }

    public final void actionPerformed(final ActionEvent e) {
        final IFileInstance instance = AreaManager.getInstance().getActiveInstance();
        if (instance == null)
            return;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ((RiderArea) instance.getRiderArea()).setViewBorder(borderType, true);
                getManagerDirector().getPluginToolsManager().getActiveInstance().updateData();
            }
        });
    }

}
