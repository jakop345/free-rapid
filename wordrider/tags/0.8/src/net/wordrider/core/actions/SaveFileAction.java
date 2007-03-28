package net.wordrider.core.actions;

import net.wordrider.core.managers.AreaManager;
import net.wordrider.core.managers.interfaces.IFileInstance;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * @author Vity
 */
public final class SaveFileAction extends CoreAction {
    private static final SaveFileAction instance = new SaveFileAction();
    private static final String CODE = "SaveFileAction";

    public static SaveFileAction getInstance() {
        return instance;
    }

    private SaveFileAction() {
        super(CODE, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK), "save.gif");
    }

    public static boolean save(final boolean showInfoWindow) {
        final IFileInstance instance = AreaManager.getInstance().getActiveInstance();
        if (instance == null)
            return false;
        if (!instance.hasAssignedFile()) {
            //not saved yet
            return SaveAsFileAction.saveAsProcess(showInfoWindow);
            //modified
        } else
            return SaveAsFileAction.runSaveProcess(getMainFrame(), instance.getFile(), instance, showInfoWindow);

    }

    public final void actionPerformed(final ActionEvent e) {
        save(true);
    }

}
