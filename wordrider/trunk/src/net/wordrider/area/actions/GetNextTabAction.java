package net.wordrider.area.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * @author Vity
 */
public final class GetNextTabAction extends TextAreaAction {
    private final static GetNextTabAction instance = new GetNextTabAction();
    private static final String CODE = "GetNextTabAction";

    private GetNextTabAction() {
        super(CODE, KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.ALT_DOWN_MASK), null);    //call to super
    }

    public static GetNextTabAction getInstance() {
        return instance;
    }


    @Override
    public boolean isEnabled() {
        return getAreaManager().getOpenedInstanceCount() > 1;
    }

    public final void actionPerformed(final ActionEvent e) {
        getAreaManager().getNextTab();
    }
}
