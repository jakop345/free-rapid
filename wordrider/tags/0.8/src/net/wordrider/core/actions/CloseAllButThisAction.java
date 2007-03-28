package net.wordrider.core.actions;

import net.wordrider.core.managers.AreaManager;
import net.wordrider.core.managers.FileInstance;

import java.awt.event.ActionEvent;

/**
 * @author Vity
 */
public final class CloseAllButThisAction extends CoreAction {
    private static final CloseAllButThisAction instance = new CloseAllButThisAction();
    private static final String CODE = "CloseAllButThisAction";

    private CloseAllButThisAction() {
        super(CODE, null, null);
    }


    public static CloseAllButThisAction getInstance() {
        return instance;
    }

    @Override
    public boolean isEnabled() {
        return AreaManager.getInstance().getOpenedInstanceCount() > 1;
    }


    private static boolean closeAll() {
        final AreaManager areaManager = AreaManager.getInstance();
        final FileInstance activeInstance = areaManager.getActiveInstance();
        //closes all except the selected on the list
        for (FileInstance instance : areaManager.getOpenedInstances()) {
            if (!instance.equals(activeInstance) && !instance.isModified()) { //is not modified list
                areaManager.closeInstanceHard(instance);
            }
        }
        for (FileInstance instance : areaManager.getOpenedInstances()) {
            if (!instance.equals(activeInstance)) {
                areaManager.setActivateFileInstance(instance);
                if (!SaveFileAction.save(false))
                    return false;
                areaManager.closeActiveInstance();
            }
        }
        return true;
    }

    public final void actionPerformed(final ActionEvent e) {
        closeAll();
    }
}
