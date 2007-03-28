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
public final class ChangeSubscriptStyleAction extends StyledAreaAction {
    private static final ChangeSubscriptStyleAction INSTANCE = new ChangeSubscriptStyleAction();
    private static final String CODE = "ChangeSubscriptStyleAction";

    public static ChangeSubscriptStyleAction getInstance() {
        return INSTANCE;
    }

    private ChangeSubscriptStyleAction() {
        super(CODE, KeyStroke.getKeyStroke(KeyEvent.VK_9, InputEvent.CTRL_MASK), "subscr.gif");
    }

    public final void actionPerformed(final ActionEvent e) {
        super.actionPerformed(e);
        final RiderArea area = getRiderArea(e);
        if (area != null) {
            final MutableAttributeSet inputAttributes = area.getInputAttributes();
            if (RiderStyles.isSubscript(inputAttributes)) {
                if (RiderStyles.isSize(inputAttributes, RiderStyles.SIZE_MINI))
                    setCharacterAttributes(area, RiderStyles.normalStyle, false);
                else {
                    setCharacterAttributes(area, RiderStyles.maxiStyle, false);
                }
            } else
                setCharacterAttributes(area, RiderStyles.miniStyle, false);
            setCharacterAttributes(area, RiderStyles.updateSubscriptAttributes(inputAttributes), false);
        }
    }


}
