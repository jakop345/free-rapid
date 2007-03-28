package net.wordrider.area.actions;

import net.wordrider.area.RiderStyles;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * @author Vity
 */
public final class AligmentRightAction extends ChangeParagraphStyleAction {
    private static final AligmentRightAction instance = new AligmentRightAction();
    private static final String CODE = "AligmentRightAction";

    public static AligmentRightAction getInstance() {
        return instance;
    }

    private AligmentRightAction() {
        super(CODE, RiderStyles.STYLE_ALIGMENT_RIGHT, KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK), "right.gif");    //call to super
    }
}
