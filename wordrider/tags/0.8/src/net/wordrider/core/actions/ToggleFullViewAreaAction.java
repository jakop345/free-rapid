package net.wordrider.core.actions;

import net.wordrider.area.RiderArea;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * @author Vity
 */
public final class ToggleFullViewAreaAction extends ToggleViewAreaAction {
    private final static ToggleFullViewAreaAction instance = new ToggleFullViewAreaAction();
    private final static String CODE = "ToggleFullViewAreaAction";

    public static ToggleFullViewAreaAction getInstance() {
        return instance;
    }

    private ToggleFullViewAreaAction() {
        super(CODE, KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK), null);    //call to super
        this.borderType = RiderArea.FULLVIEWBORDER;
    }

}
