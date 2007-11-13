package net.wordrider.core.actions;

import net.wordrider.utilities.Consts;

/**
 * @author Vity
 */
public class VisitHomepageAction extends WebAction {
    private final static VisitHomepageAction instance = new VisitHomepageAction();

    private VisitHomepageAction() {
        super("VisitHomepageAction", null, null, Consts.WEBURL);
    }

    public static VisitHomepageAction getInstance() {
        return instance;
    }

}
