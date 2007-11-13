package net.wordrider.area.actions;

import net.wordrider.area.RiderStyles;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * @author Vity
 */
public final class Margin30Action extends ChangeParagraphStyleAction {
    private static final Margin30Action instance = new Margin30Action();
    private static final String CODE = "Margin30Action";

    public static Margin30Action getInstance() {
        return instance;
    }

    private Margin30Action() {
        super(CODE, RiderStyles.STYLE_MARGIN30, KeyStroke.getKeyStroke(KeyEvent.VK_3, InputEvent.ALT_MASK), "marg_30.gif");    //call to super
    }
}
