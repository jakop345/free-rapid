package net.wordrider.core.actions;

import net.wordrider.area.RiderArea;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * @author Vity
 */
public final class Toggle89ViewAreaAction extends ToggleViewAreaAction {
    private final static Toggle89ViewAreaAction instance = new Toggle89ViewAreaAction();
    private final static String CODE = "Toggle89ViewAreaAction";

    public static Toggle89ViewAreaAction getInstance() {
        return instance;
    }

    private Toggle89ViewAreaAction() {
        super(CODE, KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK), null);    //call to super
        borderType = RiderArea.TI89VIEWBORDER;
    }

}
