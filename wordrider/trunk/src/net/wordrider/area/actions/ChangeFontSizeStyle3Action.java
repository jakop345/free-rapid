package net.wordrider.area.actions;

import net.wordrider.area.RiderStyles;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * @author Vity
 */
public final class ChangeFontSizeStyle3Action extends ChangeFontSizeStyleAction {
    private static final ChangeFontSizeStyle3Action instance = new ChangeFontSizeStyle3Action();
    private static final String CODE = "ChangeFontSizeStyle3Action";


    private ChangeFontSizeStyle3Action() {
        super(RiderStyles.STYLE_FONT_MAXI, CODE, KeyStroke.getKeyStroke(KeyEvent.VK_3, InputEvent.CTRL_MASK), "big_txt.gif");
    }

    public static ChangeFontSizeStyle3Action getInstance() {
        return instance;
    }
}
