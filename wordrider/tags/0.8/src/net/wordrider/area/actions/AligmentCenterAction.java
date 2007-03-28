package net.wordrider.area.actions;

import net.wordrider.area.RiderStyles;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * @author Vity
 */
public final class AligmentCenterAction extends ChangeParagraphStyleAction {
    private static final AligmentCenterAction instance = new AligmentCenterAction();
    private static final String CODE = "AligmentCenterAction";

    public static AligmentCenterAction getInstance() {
        return instance;
    }

    private AligmentCenterAction() {
        super(CODE, RiderStyles.STYLE_ALIGMENT_CENTER, KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK), "center.gif");    //call to super
    }
}
