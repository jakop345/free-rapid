package cz.green.swing;

import java.awt.*;
import java.util.Hashtable;

public class CountLayout implements LayoutManager2 {
    protected Hashtable comps = new Hashtable();
    protected Dimension min = new Dimension(0, 0);

    public CountLayout() {
    }

    public CountLayout(int minX, int minY) {
        min.width = minX;
        min.height = minY;
    }

    public CountLayout(Dimension min) {
        this.min = min;
    }

    public void addLayoutComponent(Component comp, Object constraints) {
        if (constraints instanceof BoundsConstraint) {
            comps.put(comp, constraints);
        }
    }

    public void addLayoutComponent(String name, Component comp) {
    }

    public float getLayoutAlignmentX(Container target) {
        return (float) 0.5;
    }

    public float getLayoutAlignmentY(Container target) {
        return (float) 0.5;
    }

    /**
     * This method was created in VisualAge.
     *
     * @param parent java.awt.Container
     * @return java.awt.Dimension
     */
    protected Dimension increaseInsets(Container parent, Dimension dim) {
        Insets i = parent.getInsets();
        dim.height += i.top + i.bottom;
        dim.width += i.left + i.right;
        return dim;
    }

    public void invalidateLayout(Container target) {
    }

    public void layoutContainer(Container parent) {
        Component[] all = parent.getComponents();
        BoundsConstraint bc;
        Dimension size = parent.getSize();
        Insets ins = parent.getInsets();
        size.width -= ins.left + ins.right;
        size.height -= ins.top + ins.bottom;
        for (int i = all.length - 1; i >= 0; i--) {
            if ((bc = (BoundsConstraint) comps.get(all[i])) != null)
                all[i].setBounds(bc.getBounds(size));
        }
    }

    public Dimension maximumLayoutSize(Container target) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public Dimension minimumLayoutSize(Container parent) {
        Component[] all = parent.getComponents();
        Dimension dim = new Dimension(min), help;
        for (int i = all.length - 1; i >= 0; i--) {
            if (all[i].isVisible()) {
                BoundsConstraint bc = (BoundsConstraint) comps.get(all[i]);
                if (bc == null) {
                    help = all[i].getMinimumSize();
                } else {
                    help = bc.getParentSize(all[i].getMinimumSize());
                }
                dim.width = Math.max(help.width, dim.width);
                dim.height = Math.max(help.height, dim.height);
            }
        }
        return increaseInsets(parent, dim);
    }

    public Dimension preferredLayoutSize(Container parent) {
        Component[] all = parent.getComponents();
        Dimension dim = new Dimension(min), help;
        for (int i = all.length - 1; i >= 0; i--) {
            if (all[i].isVisible()) {
                BoundsConstraint bc = (BoundsConstraint) comps.get(all[i]);
                if (bc == null) {
                    help = all[i].getPreferredSize();
                } else {
                    help = bc.getParentSize(all[i].getPreferredSize());
                }
                dim.width = Math.max(help.width, dim.width);
                dim.height = Math.max(help.height, dim.height);
            }
        }
        return increaseInsets(parent, dim);
    }

    public void removeLayoutComponent(Component comp) {
        comps.remove(comp);
    }

    public String toString() {
        return getClass().getName();
    }
}
