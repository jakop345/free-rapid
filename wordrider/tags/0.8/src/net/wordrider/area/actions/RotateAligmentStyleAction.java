package net.wordrider.area.actions;

import net.wordrider.area.RiderArea;
import net.wordrider.area.RiderStyles;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import java.awt.event.ActionEvent;

/**
 * @author Vity
 */
public final class RotateAligmentStyleAction extends StyledAreaAction {
    private final static RotateAligmentStyleAction instance = new RotateAligmentStyleAction();
    private final static String CODE = "RotateAligmentStyleAction";

    private RotateAligmentStyleAction() {
        super(CODE, KeyStroke.getKeyStroke("F4"), null);
    }

    public static RotateAligmentStyleAction getInstance() {
        return instance;
    }

    public final void actionPerformed(final ActionEvent e) {
        super.actionPerformed(e);
        final RiderArea area = getRiderArea(e);
        final AttributeSet set;
        final Element paraElement = area.getStyledDocument().getParagraphElement(area.getCaretPosition());
        if (!RiderStyles.isReadonlySection(paraElement) && !RiderStyles.isMath(paraElement)) {
            switch (RiderStyles.getAlignment(area.getParagraphAttributes())) {
                case RiderStyles.ALIGN_LEFT:
                    set = RiderStyles.alignmentCenteredStyle;
                    break;
                case RiderStyles.ALIGN_CENTER:
                    set = RiderStyles.alignmentRightStyle;
                    break;
                default:
                    set = RiderStyles.alignmentLeftStyle;
                    break;
            }
            setParagraphAttributes(area, set, false);
        }
    }


}
