package net.wordrider.area.actions;

import net.wordrider.area.RiderStyles;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * @author Vity
 */
public final class InsertSeparatorSingleAction extends InsertSeparateLineAction {
    private static final String CODE = "InsertSeparatorSingleAction";
    private static final InsertSeparatorSingleAction instance = new InsertSeparatorSingleAction();

    private InsertSeparatorSingleAction() {
        super(RiderStyles.SINGLE_LINE, CODE, KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.CTRL_MASK), "separ_1.gif");
    }

    public static InsertSeparatorSingleAction getInstance() {
        return instance;
    }
}
