package net.wordrider.area;

/**
 * @author Vity
 */

import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

final class AreaBorder extends EmptyBorder implements PropertyChangeListener {
    public static final int SHADOW_BAR_WIDTH = 25;
    //private static Color LINE_BORDER_COLOR;
    //private static final Color LINE_BORDER_COLOR = Color.BLACK;
    // private static Color DOTTED_LINE_BORDER_COLOR;//new Color(0x808080);
    //private static final Color DOTTED_LINE_BORDER_COLOR = Color.BLACK;
    private final RiderArea component;

    private final static Stroke DOTTED_STROKE_BORDER = new BasicStroke(1f, BasicStroke.CAP_SQUARE, BasicStroke.CAP_SQUARE, 0f, new float[]{1.5f}, 1.8f);


    public AreaBorder(final int top, final int left, final int bottom, final int right, final RiderArea riderArea) {
        super(top, left, bottom, right);
        this.component = riderArea;
        //        updateColors();
    }

    //    public final void setRightInset (final int rightInset) {
    //       // this.right = rightInset;
    //    }

    private int getRightInset() {
        if (component.limitRightBorder()) {
            final int width = component.getWidth();
            return (width <= 0) ? 0 : width - component.getMaxTextWidth() - left;
        } else
            return right;
    }

    public final Insets getBorderInsets(final Component c, final Insets insets) {
        insets.left = left;
        insets.top = top;
        insets.right = getRightInset();
        insets.bottom = bottom;
        return insets;
    }

    /**
     * Returns the insets of the border.
     */
    public final Insets getBorderInsets() {
        return new Insets(top, left, bottom, getRightInset());
        //  return new Insets(top, left, bottom, right);
    }


    public final void paintBorder(final Component c, final Graphics g, final int x, final int y, final int width, final int height) {
        super.paintBorder(c, g, x, y, width, height);    //call to super
        final Graphics2D g2 = (Graphics2D) g;
        g.setPaintMode();
        final Color bg = ColorStyles.getColor(ColorStyles.COLOR_AREA_BG);
        //        g.setColor(bg);
        //        g.fillRect(0, height - bottom, width + 1, height);
        g.setColor(ColorStyles.getBorderColor());
        g.setXORMode(bg);
        g.setColor(ColorStyles.getBorderColor());
        g.fillRect(0, y, SHADOW_BAR_WIDTH, height);
        g2.setPaintMode();
        g2.setColor(ColorStyles.getColor(ColorStyles.COLOR_BORDER_LINE_COLOR));
        if (component.showRightBorder()) {
            final int rightPosition = component.getInsets().left + component.getMaxTextWidth() + 1;
            g2.drawLine(rightPosition, y, rightPosition, height);
        }
        g2.setStroke(DOTTED_STROKE_BORDER);
        g2.drawLine(SHADOW_BAR_WIDTH - 1, y, SHADOW_BAR_WIDTH - 1, height);
    }

    public void propertyChange(final PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("lookAndFeel")) {
            ColorStyles.updateBorderColor();
            component.validate();
            component.repaint();
        }
    }
}


































