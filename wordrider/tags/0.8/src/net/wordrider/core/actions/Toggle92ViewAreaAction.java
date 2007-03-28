package net.wordrider.core.actions;

import net.wordrider.area.RiderArea;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * @author Vity
 */
public final class Toggle92ViewAreaAction extends ToggleViewAreaAction {
    private final static Toggle92ViewAreaAction instance = new Toggle92ViewAreaAction();
    private final static String CODE = "Toggle92ViewAreaAction";

    public static Toggle92ViewAreaAction getInstance() {
        return instance;
    }

    private Toggle92ViewAreaAction() {
        super(CODE, KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK), null);    //call to super
        this.borderType = RiderArea.TI92VIEWBORDER;
    }

}
