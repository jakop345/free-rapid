package net.wordrider.area;

import net.wordrider.utilities.LogUtils;

import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.logging.Logger;

/**
 * @author Vity
 */
/*
*  Paint a horizontal line the width of a column and 1 pixel high
*/
final class OvertypeCaret extends RiderCaret {
    private final static Logger logger = Logger.getLogger(OvertypeCaret.class.getName());
    /*
     *  The overtype caretPosition will simply be a horizontal line one pixel high
     *  (once we determine where to paint it)
     */
    public final void paint(final Graphics g) {
        if (isVisible()) {
            try {
                final JTextComponent component = getComponent();
                final TextUI mapper = component.getUI();
                final Rectangle r = mapper.modelToView(component, getDot());
                final Color caretColor = component.getCaretColor();
                g.setColor(caretColor);
                final int width = g.getFontMetrics().charWidth('w');
                final int y = r.y + r.height - 2;
                g.drawLine(r.x, y, r.x + width - 2, y);
            } catch (BadLocationException e) {
                LogUtils.processException(logger, e);
            }
        }
    }

    /*
     *  Damage must be overridden whenever the paint method is overridden
     *  (The damaged net.wordrider.area is the net.wordrider.area the caretPosition is painted in. We must
     *  consider the net.wordrider.area for the default caretPosition and this caretPosition)
     */
    protected final synchronized void damage(final Rectangle r) {
        if (r != null) {
            final JTextComponent component = getComponent();
            x = r.x;
            y = r.y;
            width = component.getFontMetrics(component.getFont()).charWidth('w');
            height = r.height;
            repaint();
        }
    }
}
