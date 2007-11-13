package net.wordrider.area.actions;

import net.wordrider.area.RiderArea;
import net.wordrider.area.RiderStyles;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * @author Vity
 */
public final class ChangeInvertStyleAction extends StyledAreaAction {
    private static final ChangeInvertStyleAction instance = new ChangeInvertStyleAction();
    private static final String CODE = "ChangeInvertStyleAction";

    public static ChangeInvertStyleAction getInstance() {
        return instance;
    }

    private ChangeInvertStyleAction() {

        super(CODE, KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_MASK), "inverted.gif");
    }

    public final void actionPerformed(final ActionEvent e) {
        super.actionPerformed(e);
        final RiderArea area = getRiderArea(e);
        if (area != null)
            setCharacterAttributes(area, RiderStyles.updateInvertAttributes(area.getInputAttributes()), false);
    }
}
