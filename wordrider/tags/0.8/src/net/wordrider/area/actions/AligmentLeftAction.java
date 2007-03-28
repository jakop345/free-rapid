package net.wordrider.area.actions;

import net.wordrider.area.RiderStyles;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * @author Vity
 */
public final class AligmentLeftAction extends ChangeParagraphStyleAction {
    private static final AligmentLeftAction instance = new AligmentLeftAction();
    private static final String CODE = "AligmentLeftAction";

    public static AligmentLeftAction getInstance() {
        return instance;
    }

    private AligmentLeftAction() {
        super(CODE, RiderStyles.STYLE_ALIGMENT_LEFT, KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_MASK), "left.gif");    //call to super
    }
}
