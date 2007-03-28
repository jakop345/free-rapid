package net.wordrider.core.actions;

import net.wordrider.utilities.Consts;

/**
 * @author Vity
 */
public class OnlineDocumentationAction extends WebAction {
    private final static OnlineDocumentationAction instance = new OnlineDocumentationAction();

    private OnlineDocumentationAction() {
        super("OnlineDocumentationAction", null, null, Consts.ONLINE_DOCUMENTATION);
    }

    public static OnlineDocumentationAction getInstance() {
        return instance;
    }

}
