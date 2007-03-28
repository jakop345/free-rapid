package net.wordrider.area.actions;

import net.wordrider.area.RiderStyles;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * @author Vity
 */
public final class ChangeFontSizeStyle1Action extends ChangeFontSizeStyleAction {
    private static final ChangeFontSizeStyle1Action instance = new ChangeFontSizeStyle1Action();
    private static final String CODE = "ChangeFontSizeStyle1Action";

    private ChangeFontSizeStyle1Action() {
        super(RiderStyles.STYLE_FONT_MINI, CODE, KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.CTRL_MASK), "sm_txt.gif");
    }

    public static ChangeFontSizeStyle1Action getInstance() {
        return instance;
    }
}
