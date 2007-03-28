package net.wordrider.dialogs.settings;

import javax.swing.*;
import java.awt.*;

/**
 * @author Vity
 */
final class SelectablePanel extends JPanel {
    public SelectablePanel() {
        super(new SettingsCardLayout());
    }

    private static final class SettingsCardLayout implements LayoutManager {
        public final void addLayoutComponent(final String name, final Component child) {
            if (name != null) {
                child.setName(name);
            }
            child.setVisible(child.getParent().getComponentCount() == 1);
        }

        public final void removeLayoutComponent(final Component child) {
            if (child.isVisible()) {
                final Container parent = child.getParent();
                if (parent.getComponentCount() > 0) {
                    parent.getComponent(0).setVisible(true);
                }
            }
        }

        public final Dimension preferredLayoutSize(final Container parent) {
            final int nChildren = parent.getComponentCount();
            final Insets insets = parent.getInsets();
            int width = insets.left + insets.right;
            int height = insets.top + insets.bottom;

            for (int i = 0; i < nChildren; i++) {
                final Component comp = parent.getComponent(i);
                if (comp.isVisible()) {
                    final Dimension d = comp.getPreferredSize();
                    if (d.width > width) {
                        width = d.width;
                    }
                    if (d.height > height) {
                        height = d.height;
                    }
                }
            }
            return new Dimension(width, height);
        }

        public final Dimension minimumLayoutSize(final Container parent) {
            final int nChildren = parent.getComponentCount();
            final Insets insets = parent.getInsets();
            int width = insets.left + insets.right;
            int height = insets.top + insets.bottom;

            for (int i = 0; i < nChildren; i++) {
                final Component comp = parent.getComponent(i);
                if (comp.isVisible()) {
                    final Dimension d = comp.getMinimumSize();
                    if (d.width > width) {
                        width = d.width;
                    }
                    if (d.height > height) {
                        height = d.height;
                    }
                }
            }
            return new Dimension(width, height);
        }

        public final void layoutContainer(final Container parent) {
            final int nChildren = parent.getComponentCount();
            final Insets insets = parent.getInsets();
            for (int i = 0; i < nChildren; i++) {
                final Component child = parent.getComponent(i);
                if (child.isVisible()) {
                    final Rectangle r = parent.getBounds();
                    final int width = r.width - insets.left + insets.right;
                    final int height = r.height - insets.top + insets.bottom;
                    child.setBounds(insets.left, insets.top, width, height);
                    break;
                }
            }
        }
    }


    private int getVisibleChildIndex() {
        final int nChildren = getComponentCount();
        for (int i = 0; i < nChildren; i++) {
            final Component child = getComponent(i);
            if (child.isVisible()) {
                return i;
            }
        }
        return -1;
    }

    public final void showCard(final Component card) {
        if (card.getParent() == null || !card.getParent().equals(this)) {
            add(card);
        }
        final int index = getVisibleChildIndex();
        if (index != -1) {
            getComponent(index).setVisible(false);
            //remove(getComponent(index));
        }
        card.setVisible(true);
        revalidate();
        repaint();
    }

    // --Commented out by Inspection START (26.2.05 18:27):
    //    public final void showCard(final String name) {
    //        final int nChildren = getComponentCount();
    //        for (int i = 0; i < nChildren; i++) {
    //            final Component child = getComponent(i);
    //            if (child.getName().equals(name)) {
    //                showCard(child);
    //                break;
    //            }
    //        }
    //    }
    // --Commented out by Inspection STOP (26.2.05 18:27)
    public final Component getActiveCard() {
        final int index = getVisibleChildIndex();
        if (index != -1) {
            return getComponent(index);
        } else
            return null;
    }


}
