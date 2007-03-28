package net.wordrider.area.actions;

import net.wordrider.area.RiderStyles;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * @author Vity
 */
public final class Margin20Action extends ChangeParagraphStyleAction {
    private static final Margin20Action instance = new Margin20Action();
    private static final String CODE = "Margin20Action";

    public static Margin20Action getInstance() {
        return instance;
    }

    private Margin20Action() {
        super(CODE, RiderStyles.STYLE_MARGIN20, KeyStroke.getKeyStroke(KeyEvent.VK_2, InputEvent.ALT_MASK), "marg_20.gif");    //call to super
    }
}
