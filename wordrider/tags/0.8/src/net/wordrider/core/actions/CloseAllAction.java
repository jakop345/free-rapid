package net.wordrider.core.actions;

import net.wordrider.core.managers.AreaManager;
import net.wordrider.core.managers.FileInstance;
import net.wordrider.dialogs.CloseDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Collection;

/**
 * @author Vity
 */
public final class CloseAllAction extends CoreAction {
    private static final CloseAllAction instance = new CloseAllAction();

    private CloseAllAction() {
        super("CloseAllAction", KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK), null);
    }


    public static CloseAllAction getInstance() {
        return instance;
    }

    public static boolean closeAll() {
        final AreaManager areaManager = AreaManager.getInstance();
        final Collection<FileInstance> modifiedList = areaManager.getModifiedInstances();
        if (!modifiedList.isEmpty()) {
            final CloseDialog<FileInstance> dialog = new CloseDialog<FileInstance>(getMainFrame(), modifiedList);
            if (dialog.getModalResult() != CloseDialog.RESULT_OK)
                return false;
            final Collection<FileInstance> selectedList = dialog.getReturnList();
            if (selectedList != null) {
                //closes all except the selected on the list
                for (FileInstance o : areaManager.getOpenedInstances()) {
                    if (!selectedList.contains(o)) { //is not modified list
                        areaManager.closeInstanceHard(o);
                    }
                }
                for (FileInstance aSelectedList : selectedList) {
                    areaManager.setActivateFileInstance(aSelectedList);
                    if (!SaveFileAction.save(false))
                        return false;
                    areaManager.closeActiveInstance();
                }
            } else
                return false;//user set doClose
        } else {
            for (FileInstance o : areaManager.getOpenedInstances()) {
                areaManager.closeInstanceHard(o);
                //it.remove();
            }
        }
        return true;
    }

    public final void actionPerformed(final ActionEvent e) {
        closeAll();
    }
}
