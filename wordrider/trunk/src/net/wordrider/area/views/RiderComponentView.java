package net.wordrider.area.views;

import javax.swing.*;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import java.awt.*;

/**
 * @author Vity
 */
final class RiderComponentView extends ComponentView {
    public final void paint(final Graphics g, final Shape a) {
        ((JComponent) getComponent()).putClientProperty("selected", RiderViewFactory.isInSelection((JTextComponent) getContainer(), getElement()));
        super.paint(g, a);    //call to super
    }

    public final float getAlignment(final int axis) {
        return 0.5F;//super.getAlignment(axis);    //call to super
    }

    public RiderComponentView(final Element elem) {
        super(elem);
    }
}
