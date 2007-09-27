package cz.cvut.felk.timejuggler.core.tasks;

import application.Application;
import application.ResourceMap;
import application.Task;

/**
 * @author Vity
 */
abstract class CoreTask<T, V> extends Task<T, V> {
    static InputBlocker inputBlocker = null;

    public CoreTask(Application application, Class clazz) {
        super(application, Application.getInstance().getContext().getResourceMap(clazz, CoreTask.class), null);
        setDefaultInputBlocker();
    }

    public ResourceMap getTaskResourceMap() {
        return super.getResourceMap();
    }

    private void setDefaultInputBlocker() {
        if (inputBlocker == null)
            inputBlocker = new ScreenInputBlocker(this, BlockingScope.APPLICATION, null);
        this.setInputBlocker(inputBlocker);
    }
}