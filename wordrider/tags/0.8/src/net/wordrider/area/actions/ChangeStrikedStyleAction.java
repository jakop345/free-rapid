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
public final class ChangeStrikedStyleAction extends StyledAreaAction {
    private static final ChangeStrikedStyleAction instance = new ChangeStrikedStyleAction();
    private static final String CODE = "ChangeStrikedStyleAction";

    public static ChangeStrikedStyleAction getInstance() {
        return instance;
    }

    private ChangeStrikedStyleAction() {
        super(CODE, KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.CTRL_MASK), "strike_out.gif");
    }

    public final void actionPerformed(final ActionEvent e) {
        super.actionPerformed(e);
        final RiderArea area = getRiderArea(e);
        if (area != null)
            setCharacterAttributes(area, RiderStyles.updateStrikedAttributes(area.getInputAttributes()), false);
    }


}
