package cz.cvut.felk.erm.core.tasks;

import org.jdesktop.application.Application;
import org.jdesktop.application.Task;

/**
 * @author Ladislav Vitasek
 */
abstract class CoreTask<T, V> extends Task<T, V> {
    static InputBlocker inputBlocker = null;

    public CoreTask(Application application) {
        super(application);
        //     setDefaultInputBlocker();
    }

//    public ResourceMap getTaskResourceMap() {
//        return super.getResourceMap();
//    }

//    private void setDefaultInputBlocker() {
//        if (inputBlocker == null)
//            inputBlocker = new ScreenInputBlocker(this, BlockingScope.APPLICATION, null);
//        this.setInputBlocker(inputBlocker);
//    }
}