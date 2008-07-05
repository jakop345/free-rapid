package cz.cvut.felk.erm.core.tasks;

import cz.cvut.felk.erm.core.application.GlobalEDTExceptionHandler;
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

    @Override
    protected void failed(Throwable cause) {
        super.failed(cause);
        handleRuntimeException(cause);
    }

    protected boolean handleRuntimeException(Throwable cause) {
        if (cause instanceof RuntimeException) {
            GlobalEDTExceptionHandler exceptionHandler = new GlobalEDTExceptionHandler();
            exceptionHandler.uncaughtException(Thread.currentThread(), cause);
            return true;
        }
        return false;
    }
}