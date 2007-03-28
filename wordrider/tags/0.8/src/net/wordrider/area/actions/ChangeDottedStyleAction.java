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
public final class ChangeDottedStyleAction extends StyledAreaAction {
    private static final ChangeDottedStyleAction instance = new ChangeDottedStyleAction();
    private static final String CODE = "ChangeDottedStyleAction";

    public static ChangeDottedStyleAction getInstance() {
        return instance;
    }

    private ChangeDottedStyleAction() {
        super(CODE, KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_MASK | InputEvent.ALT_MASK), "uuderl_dos.gif");
    }

    public final void actionPerformed(final ActionEvent e) {
        super.actionPerformed(e);
        final RiderArea area = getRiderArea(e);
        if (area != null)
            setCharacterAttributes(area, RiderStyles.flipUnderlineDottedAttributes(area.getInputAttributes()), false);
    }


}
