package net.wordrider.core.actions;

import java.awt.event.ActionEvent;

/**
 * @author Vity
 */
public final class RunGCAction extends CoreAction {
    private final static RunGCAction instance = new RunGCAction();
    private static final String CODE = "RunGCAction";

    public static RunGCAction getInstance() {
        return instance;
    }

    private RunGCAction() {
        super(CODE, null, null);    //call to super
    }

    public final void actionPerformed(final ActionEvent e) {
        System.gc();
    }
}
