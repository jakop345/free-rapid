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
public final class ChangeUnderlineStyleAction extends StyledAreaAction {
    private static final ChangeUnderlineStyleAction instance = new ChangeUnderlineStyleAction();
    private static final String CODE = "ChangeUnderlineStyleAction";

    public static ChangeUnderlineStyleAction getInstance() {
        return instance;
    }

    private ChangeUnderlineStyleAction() {
        super(CODE, KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_MASK), "undeline.gif");
    }

    public final void actionPerformed(final ActionEvent e) {
        super.actionPerformed(e);
        final RiderArea area = getRiderArea(e);
        if (area != null) {
            //final MutableAttributeSet set = RiderStyles.setProperty(new SimpleAttributeSet(), RiderStyles.STYLE_UNDERLINEDOTTED, false);
            //net.wordrider.area.getInputAttributes().removeAttribute(RiderStyles.STYLE_UNDERLINEDOTTED);
            setCharacterAttributes(area, RiderStyles.flipUnderlineAttributes(area.getInputAttributes()), false);
        }
    }


}
