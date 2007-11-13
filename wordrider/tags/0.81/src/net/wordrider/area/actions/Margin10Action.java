package net.wordrider.area.actions;

import net.wordrider.area.RiderStyles;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * @author Vity
 */
public final class Margin10Action extends ChangeParagraphStyleAction {
    private static final Margin10Action instance = new Margin10Action();
    private static final String CODE = "Margin10Action";

    public static Margin10Action getInstance() {
        return instance;
    }

    private Margin10Action() {
        super(CODE, RiderStyles.STYLE_MARGIN10, KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.ALT_MASK), "marg_10.gif");    //call to super
    }
}
