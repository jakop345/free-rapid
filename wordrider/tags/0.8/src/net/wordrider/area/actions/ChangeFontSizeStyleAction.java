package net.wordrider.area.actions;

import net.wordrider.area.RiderArea;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author Vity
 */
abstract class ChangeFontSizeStyleAction extends StyledAreaAction {
    private final String style;

    ChangeFontSizeStyleAction(final String style, final String actionCode, final KeyStroke keyStroke, final String smallIcon) {
        super(actionCode, keyStroke, smallIcon);
        this.style = style;
    }

    public final void actionPerformed(final ActionEvent e) {
        super.actionPerformed(e);
        final RiderArea area = getRiderArea(e);
        if (area != null)
            setCharacterAttributes(area, area.getDoc().getStyle(style), false);
    }


}
