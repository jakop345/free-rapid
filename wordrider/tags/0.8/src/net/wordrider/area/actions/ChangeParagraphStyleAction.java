package net.wordrider.area.actions;

import net.wordrider.area.RiderArea;

import javax.swing.*;
import javax.swing.text.Style;
import java.awt.event.ActionEvent;

/**
 * @author Vity
 */
abstract class ChangeParagraphStyleAction extends StyledAreaAction {
    private String styleCode = null;
    Style style = null;


    protected ChangeParagraphStyleAction(final String actionCode, final String styleCode, final KeyStroke keyStroke, final String smallIcon) {
        this(actionCode, keyStroke, smallIcon);
        this.styleCode = styleCode;
    }

    protected ChangeParagraphStyleAction(final String actionCode, final KeyStroke keyStroke, final String smallIcon) {
        super(actionCode, keyStroke, smallIcon);
    }

    public void actionPerformed(final ActionEvent e) {
        super.actionPerformed(e);
        final RiderArea area = getRiderArea(e);
        if (area != null && (styleCode != null || style != null)) {
            setParagraphAttributes(area, (style != null) ? style : area.getDoc().getStyle(styleCode), false);
        }
    }


}
