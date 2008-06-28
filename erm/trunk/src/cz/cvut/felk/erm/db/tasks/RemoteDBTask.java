package cz.cvut.felk.erm.db.tasks;

import cz.cvut.felk.erm.core.tasks.CoreTask;
import cz.cvut.felk.erm.core.tasks.ScreenInputBlocker;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

/**
 * @author Ladislav Vitasek
 */
abstract class RemoteDBTask<T, V> extends CoreTask<T, V> {
    public RemoteDBTask(Application application, ResourceMap resourceMap, String resourcePrefix) {
        super(application, resourceMap, resourcePrefix);
        setDefaultInputBlocker();

    }

    public RemoteDBTask(Application application) {
        super(application);
        setDefaultInputBlocker();
    }

    private void setDefaultInputBlocker() {
        InputBlocker blocker = this.getInputBlocker();
        if (blocker == null)
            blocker = new ScreenInputBlocker(this, BlockingScope.APPLICATION, null);
        this.setInputBlocker(blocker);
    }

}
