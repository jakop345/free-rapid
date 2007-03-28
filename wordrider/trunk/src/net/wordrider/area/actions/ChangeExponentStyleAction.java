package net.wordrider.area.actions;

import net.wordrider.area.RiderArea;
import net.wordrider.area.RiderStyles;

import javax.swing.*;
import javax.swing.text.MutableAttributeSet;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * @author Vity
 */
public final class ChangeExponentStyleAction extends StyledAreaAction {
    private static final ChangeExponentStyleAction INSTANCE = new ChangeExponentStyleAction();
    private static final String CODE = "ChangeExponentStyleAction";

    public static ChangeExponentStyleAction getInstance() {
        return INSTANCE;
    }

    private ChangeExponentStyleAction() {
        super(CODE, KeyStroke.getKeyStroke(KeyEvent.VK_0, InputEvent.CTRL_MASK), "superscr.gif");
    }

    public final void actionPerformed(final ActionEvent e) {
        super.actionPerformed(e);
        final RiderArea area = getRiderArea(e);
        if (area != null) {
            final MutableAttributeSet inputAttributes = area.getInputAttributes();
            if (RiderStyles.isExposant(inputAttributes)) {
                if (RiderStyles.isSize(inputAttributes, RiderStyles.SIZE_MINI))
                    setCharacterAttributes(area, RiderStyles.normalStyle, false);
                else {
                    setCharacterAttributes(area, RiderStyles.maxiStyle, false);
                    return;
                }
            } else
                setCharacterAttributes(area, RiderStyles.miniStyle, false);

            setCharacterAttributes(area, RiderStyles.updateExposantAttributes(inputAttributes), false);
        }
    }


}
