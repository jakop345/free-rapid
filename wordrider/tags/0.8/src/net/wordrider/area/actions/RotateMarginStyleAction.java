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
public final class RotateMarginStyleAction extends StyledAreaAction {
    private static final RotateMarginStyleAction instance = new RotateMarginStyleAction();
    private final static String CODE = "RotateMarginStyleAction";

    private RotateMarginStyleAction() {
        super(CODE, KeyStroke.getKeyStroke("F3"), null);
    }

    public static RotateMarginStyleAction getInstance() {
        return instance;
    }

    public final void actionPerformed(final ActionEvent e) {
        super.actionPerformed(e);
        final RiderArea area = getRiderArea(e);
        if (area == null) return;
        final AttributeSet set;
        final Element paraElement = area.getStyledDocument().getParagraphElement(area.getCaretPosition());
        if (!RiderStyles.isReadonlySection(paraElement) && !RiderStyles.isMath(paraElement)) {
            switch (RiderStyles.getMargin(area.getParagraphAttributes())) {
                case RiderStyles.MARGIN_0:
                    set = RiderStyles.margin10Style;
                    break;
                case RiderStyles.MARGIN_10:
                    set = RiderStyles.margin20Style;
                    break;
                case RiderStyles.MARGIN_20:
                    set = RiderStyles.margin30Style;
                    break;
                default:
                    set = RiderStyles.alignmentLeftStyle;
                    break;
            }
            setParagraphAttributes(area, set, false);
        }
    }


}
