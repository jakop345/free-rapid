package net.wordrider.core.actions;

import net.wordrider.core.managers.AreaManager;
import net.wordrider.core.managers.FileInstance;

import java.awt.event.ActionEvent;

/**
 * @author Vity
 */
public final class CloseAllUnmodifiedAction extends CoreAction {
    private static final CloseAllUnmodifiedAction instance = new CloseAllUnmodifiedAction();
    private static final String CODE = "CloseAllUnmodifiedAction";

    private CloseAllUnmodifiedAction() {
        super(CODE, null, null);
    }


    public static CloseAllUnmodifiedAction getInstance() {
        return instance;
    }


    @Override
    public boolean isEnabled() {
        final AreaManager areaManager = AreaManager.getInstance();
        return areaManager.getModifiedInstances().size() != areaManager.getOpenedInstanceCount();
    }

    private static boolean closeAll() {
        final AreaManager areaManager = AreaManager.getInstance();
        for (FileInstance o : areaManager.getOpenedInstances()) {
            if (!o.isModified()) { //is not modified list
                areaManager.closeInstanceHard(o);
            }
        }
        return true;
    }

    public final void actionPerformed(final ActionEvent e) {
        closeAll();
    }
}
