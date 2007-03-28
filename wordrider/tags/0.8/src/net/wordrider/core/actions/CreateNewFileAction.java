package net.wordrider.core.actions;

import net.wordrider.core.managers.AreaManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * @author Vity
 */
public final class CreateNewFileAction extends CoreAction {
    private static final CreateNewFileAction instance = new CreateNewFileAction();

    private CreateNewFileAction() {
        super("CreateNewFileAction", KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK), "new.gif");
    }

    public static CreateNewFileAction getInstance() {
        return instance;
    }

    public final void actionPerformed(final ActionEvent e) {
        final AreaManager areaManager = AreaManager.getInstance();
        areaManager.openFileInstance();
        areaManager.grabActiveFocus();
    }
}
