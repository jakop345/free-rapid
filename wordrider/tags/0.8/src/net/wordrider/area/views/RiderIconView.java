package net.wordrider.area.views;

import net.wordrider.area.RiderArea;
import net.wordrider.area.RiderStyles;

import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.Position;
import javax.swing.text.View;
import java.awt.*;

/**
 * @author Vity
 */
final class RiderIconView extends IconView {
    private boolean wasInSelection;
    private static final Stroke SELECTION_STROKE = new BasicStroke(3);

    public RiderIconView(final Element elem) {
        super(elem);
    }

    public float getAlignment(final int axis) {
        switch (axis) {
            case View.Y_AXIS:
                return super.getAlignment(axis);
            default:
                return 0.5F;
        }
    }


    public final String getToolTipText(float x, float y, Shape allocation) {
        return RiderStyles.getImage(getElement()).getDescription();
    }


    public final int viewToModel(float x, float y, Shape a, Position.Bias[] bias) {
        final Rectangle alloc = (Rectangle) a;
        if (x < alloc.x + (alloc.width)) {
            bias[0] = Position.Bias.Forward;
            return getStartOffset();
        }
        bias[0] = Position.Bias.Backward;
        return getEndOffset();
    }

    public final void paint(final Graphics g, final Shape a) {
        final RiderArea area = (RiderArea) getContainer();
        final Element el = getElement();
        if (RiderViewFactory.isInSelection(area, el)) {
            super.paint(g, a);    //call to super
            //final int selStart = area.getSelectionStart();
            final int selEnd = area.getSelectionEnd();
            final Rectangle rect = a.getBounds();
            if (area.getSelectionStart() == el.getStartOffset() && selEnd == el.getEndOffset()) {
                // g.setXORMode(Color.WHITE);
                final Graphics2D g2 = (Graphics2D) g;
                g2.setPaintMode();
                g2.setColor(area.getSelectionColor());
                final Stroke backup = g2.getStroke();
                g2.setStroke(SELECTION_STROKE);
                g2.drawRect(rect.x + 1, rect.y + 1, rect.width - 3, rect.height - 1);
                g2.setStroke(backup);
            } else {
                g.setXORMode(Color.WHITE);
                g.setColor(area.getSelectedTextColor());
                g.fillRect(rect.x, rect.y, rect.width, rect.height);
            }
            wasInSelection = true;
        } else {
            if (wasInSelection) {
                wasInSelection = false;
                area.getDoc().refresh(el.getStartOffset(), el.getEndOffset() - el.getStartOffset());
                return;
            }
            super.paint(g, a);    //call to super
        }
    }
}
