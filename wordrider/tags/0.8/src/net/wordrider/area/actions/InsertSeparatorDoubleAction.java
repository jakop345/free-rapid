package net.wordrider.area.actions;

import net.wordrider.area.RiderStyles;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * @author Vity
 */
public final class InsertSeparatorDoubleAction extends InsertSeparateLineAction {
    private static final InsertSeparatorDoubleAction instance = new InsertSeparatorDoubleAction();
    private static final String CODE = "InsertSeparatorDoubleAction";

    private InsertSeparatorDoubleAction() {
        super(RiderStyles.DOUBLE_LINE, CODE, KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, InputEvent.CTRL_MASK), "separ_2.gif");
    }

    public static InsertSeparatorDoubleAction getInstance() {
        return instance;
    }
}
