package net.wordrider.core.actions;

import net.wordrider.core.Lng;
import net.wordrider.core.managers.AreaManager;
import net.wordrider.core.managers.interfaces.IFileInstance;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * @author Vity
 */
public final class CloseActiveAction extends CoreAction {
    private static final CloseActiveAction instance = new CloseActiveAction();
    private static final String CODE = "CloseActiveAction";

    private CloseActiveAction() {
        super(CODE, KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.CTRL_MASK), null);
    }


    public static CloseActiveAction getInstance() {
        return instance;
    }

    private static void closeActive() {
        //        IFileInstance instance = getInstance().getActiveInstance();
        //        if (instance != null) {
        //            if (instance.isModified()) {
        //               if (!SaveFileAction.getInstance().save(e, false)) return false;
        //            }
        //
        //        }
        //        return true;
        AreaManager.getInstance().closeActiveInstance();
    }

    public final void updateStatusName(final IFileInstance instance) {
        final String newName = (instance != null) ? Lng.getLabel(CODE + ".opened", instance.getName()) : Lng.getLabel(CODE);
        putValue(Action.NAME, newName);
    }

    public final void actionPerformed(final ActionEvent e) {
        closeActive();
    }
}
