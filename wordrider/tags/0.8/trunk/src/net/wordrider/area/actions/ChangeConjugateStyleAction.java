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
public final class ChangeConjugateStyleAction extends StyledAreaAction {
    private static final ChangeConjugateStyleAction instance = new ChangeConjugateStyleAction();
    private static final String CODE = "ChangeConjugateStyleAction";

    public static ChangeConjugateStyleAction getInstance() {
        return instance;
    }

    private ChangeConjugateStyleAction() {
        super(CODE, KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK), "conjugate.gif");
    }

    public final void actionPerformed(final ActionEvent e) {
        super.actionPerformed(e);
        final RiderArea area = getRiderArea(e);
        if (area != null)
            setCharacterAttributes(area, RiderStyles.updateConjugateAttributes(area.getInputAttributes()), false);
    }


}
