package cz.cvut.felk.erm.core.tasks;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;

/**
 * @author Ladislav Vitasek
 */
public abstract class CoreTask<T, V> extends Task<T, V> {
    static InputBlocker inputBlocker = null;

    public CoreTask(Application application, ResourceMap resourceMap, String resourcePrefix) {
        super(application, resourceMap, resourcePrefix);
    }

    public CoreTask(Application application) {
        super(application);
        //     setDefaultInputBlocker();
    }

    public ResourceMap getTaskResourceMap() {
        return super.getResourceMap();
    }

//    private void setDefaultInputBlocker() {
//        if (inputBlocker == null)
//            inputBlocker = new ScreenInputBlocker(this, BlockingScope.APPLICATION, null);
//        this.setInputBlocker(inputBlocker);
//    }

    public void postMessage(String s, Object args) {
        message(s, args);
    }
}