package net.wordrider.core.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.Autoscroll;

/**
 * @author Santhosh Kumar
 */
public class AutoscrollSupport implements Autoscroll {
    // component for which autoscroll to be enabled
    private Component comp;

    // The insets where autoscrolling is active */
    private Insets insets;

    // no of units to be scrolled in each direction
    private Insets scrollUnits;

    public AutoscrollSupport(Component comp, Insets insets) {
        this(comp, insets, insets);
    }

    private AutoscrollSupport(Component comp, Insets insets, Insets scrollUnits) {
        this.comp = comp;
        this.insets = insets;
        this.scrollUnits = scrollUnits;
    }

    public void autoscroll(Point cursorLoc) {
        JViewport viewport = getViewport();
        if (viewport == null)
            return;
        Point viewPos = viewport.getViewPosition();
        int viewHeight = viewport.getExtentSize().height;
        int viewWidth = viewport.getExtentSize().width;

        // perform scrolling
        if ((cursorLoc.y - viewPos.y) < insets.top) { // scroll up
            viewport.setViewPosition(
                    new Point(viewPos.x,
                            Math.max(viewPos.y - scrollUnits.top, 0)));
        } else if ((viewPos.y + viewHeight - cursorLoc.y) < insets.bottom) { // scroll down
            viewport.setViewPosition(
                    new Point(viewPos.x,
                            Math.min(viewPos.y + scrollUnits.bottom,
                                    comp.getHeight() - viewHeight)));
        } else if ((cursorLoc.x - viewPos.x) < insets.left) { // scroll left
            viewport.setViewPosition(
                    new Point(Math.max(viewPos.x - scrollUnits.left, 0),
                            viewPos.y));
        } else if ((viewPos.x + viewWidth - cursorLoc.x) < insets.right) { // scroll right
            viewport.setViewPosition(
                    new Point(Math.min(viewPos.x + scrollUnits.right, comp.getWidth() - viewWidth),
                            viewPos.y));
        }
    }

    public Insets getAutoscrollInsets() {
        int height = comp.getHeight();
        int width = comp.getWidth();
        return new Insets(height, width, height, width);
    }

    private JViewport getViewport() {
        return (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, comp);
    }
}