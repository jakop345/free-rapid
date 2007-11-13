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
public final class ChangeVectorStyleAction extends StyledAreaAction {
    private static final ChangeVectorStyleAction instance = new ChangeVectorStyleAction();
    private static final String CODE = "ChangeVectorStyleAction";

    public static ChangeVectorStyleAction getInstance() {
        return instance;
    }

    private ChangeVectorStyleAction() {
        super(CODE, KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_MASK), "vector.gif");
    }

    public final void actionPerformed(final ActionEvent e) {
        super.actionPerformed(e);
        final RiderArea area = getRiderArea(e);
        if (area != null)
            setCharacterAttributes(area, RiderStyles.updateVectorAttributes(area.getInputAttributes()), false);
    }


}
