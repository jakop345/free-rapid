package net.wordrider.area;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;

/**
 * @author Vity
 */
final class SeparatorLine extends JComponent {
    public static final int SINGLE_LINE = 0;
    public static final int DOUBLE_LINE = 1;
    private int lineType = SINGLE_LINE;
    private JTextComponent pane = null;
    private final static Stroke stroke = new BasicStroke(1);
    private boolean wasSelected;

    private SeparatorLine(final JTextComponent pane) {
        this.pane = pane;
        this.addHierarchyListener(new MyHierarchyListener(this, pane));
        //  this.pane = (JComponent)getParent();
    }

    public final int getLineType() {
        return lineType;
    }

    public final Dimension getPreferredSize() {
        return getMinimumSize();
    }

    public final int getWidth() {
        final Insets insets = pane.getInsets();
        return (int) pane.getVisibleRect().getWidth() - insets.left - insets.right - 2;//- pane.getBorder().getBorderInsets(pane).left - pane.getBorder().getBorderInsets(pane).right - 2;
    }

    public boolean contains(final int X, final int Y) {
        int w = getWidth();
        int h = getHeight();
        if ((w | h) < 0) {
            // At least one of the dimensions is negative...
            return false;
        }
        // Note: if either dimension is zero, tests below must return false...
        final int x = getX();
        final int y = getY() + 6;
        if (X < x || Y < y) {
            return false;
        }
        w += x;
        h += y;
        //    overflow || intersect
        return ((w < x || w > X) &&
                (h < y || h > Y));
    }


    public SeparatorLine(final JTextComponent pane, final int type) {
        this(pane);
        //  this.setBounds(0, 0, getWidth(), getHeight());
        this.lineType = type;
    }

    public final Dimension getMinimumSize() {
        return new Dimension(getWidth(), getHeight());
    }

    public final void paint(final Graphics g) {
        super.paint(g);    //call to super
        assert g != null;
        assert pane != null;
        g.setPaintMode();
        if ((Boolean) getClientProperty("selected")) {
            g.setColor(pane.getSelectionColor());
            g.fillRect(getX(), getY(), getX() + getWidth(), getY() + 6);
            wasSelected = true;
            g.setColor(Color.BLACK);
        } else {
            if (wasSelected) {
                //invalidate();
                repaint();
                wasSelected = false;
                return;
            }
            g.setColor(ColorStyles.getColor(ColorStyles.COLOR_LINE_COLOR));
        }

        final Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(stroke);
        //g.setClip(getX() + (int) (getAlignmentX()), getY() + (int) (getAlignmentY() - 2), getWidth() + (int) (getAlignmentX()), getHeight() + (int) (getAlignmentY()));

        if (lineType == SINGLE_LINE)
            g.drawRect(getX(), getY(), getX() + getWidth(), getY());
        else {
            g.drawLine(getX(), getY(), getX() + getWidth(), getY());
            g.drawLine(getX(), getY() + 2, getX() + getWidth(), getY() + 2);
        }
    }

    public final int getHeight() {
        return 8;
    }

    public final String toString() {
        return getStringRepresentation(lineType);
    }

    public static String getStringRepresentation(final int aLineType) {
        return (aLineType == SINGLE_LINE) ? "&-" : "&=";
    }

    public final class MyHierarchyListener implements HierarchyListener {
        private Container oldParent;
        private final Component comp;
        private final JTextComponent pane;

        public MyHierarchyListener(final Component comp, final JTextComponent
                textPane) {
            this.comp = comp;
            this.pane = textPane;
        }

        public final void hierarchyChanged(final HierarchyEvent e) {
            final Container parent = comp.getParent();

            if (parent != oldParent) {//hack
                if (oldParent != null) {
                    pane.remove(oldParent);
                }
                oldParent = parent;
            }
        }
    }


}
