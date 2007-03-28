package net.wordrider.area.actions;

import net.wordrider.area.RiderStyles;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * @author Vity
 */
public final class ChangeFontSizeStyle2Action extends ChangeFontSizeStyleAction {
    private static final ChangeFontSizeStyle2Action instance = new ChangeFontSizeStyle2Action();
    private static final String CODE = "ChangeFontSizeStyle2Action";

    private ChangeFontSizeStyle2Action() {
        super(RiderStyles.STYLE_FONT_NORMAL, CODE, KeyStroke.getKeyStroke(KeyEvent.VK_2, InputEvent.CTRL_MASK), "norm_txt.gif");
    }

    public static ChangeFontSizeStyle2Action getInstance() {
        return instance;
    }
}
