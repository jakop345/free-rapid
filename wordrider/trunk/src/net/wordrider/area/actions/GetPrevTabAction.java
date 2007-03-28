package net.wordrider.area.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * @author Vity
 */
public final class GetPrevTabAction extends TextAreaAction {
    private final static GetPrevTabAction instance = new GetPrevTabAction();
    private static final String CODE = "GetPrevTabAction";

    private GetPrevTabAction() {
        super(CODE, KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.ALT_DOWN_MASK), null);    //call to super
    }

    public static GetPrevTabAction getInstance() {
        return instance;
    }

    @Override
    public boolean isEnabled() {
        return getAreaManager().getOpenedInstanceCount() > 1;
    }

    public final void actionPerformed(final ActionEvent e) {
        getAreaManager().getPrevTab();
    }
}
