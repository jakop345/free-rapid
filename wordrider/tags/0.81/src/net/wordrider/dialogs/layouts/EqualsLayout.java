package net.wordrider.dialogs.layouts;

import javax.swing.*;
import java.awt.*;

/**
 * @author Santhosh Kumar - santhosh@in.fiorano.com
 */
public class EqualsLayout implements LayoutManager, SwingConstants {
    private int gap;
    private int alignment;

    public EqualsLayout(final int alignment, final int gap) {
        setGap(gap);
        setAlignment(alignment);
    }

    public EqualsLayout(int gap) {
        this(RIGHT, gap);
    }

    public int getAlignment() {
        return alignment;
    }

    private void setAlignment(int alignment) {
        this.alignment = alignment;
    }

    public int getGap() {
        return gap;
    }

    private void setGap(int gap) {
        this.gap = gap;
    }

    private Dimension[] dimensions(Component children[]) {
        int maxWidth = 0;
        int maxHeight = 0;
        int visibleCount = 0;
        Dimension componentPreferredSize;
        Dimension componentMinimumSize;

        for (int i = 0, c = children.length; i < c; i++) {
            if (children[i].isVisible()) {
                componentPreferredSize = children[i].getPreferredSize();
                componentMinimumSize = children[i].getMinimumSize();
                maxWidth = Math.max(maxWidth, Math.max(componentPreferredSize.width, componentMinimumSize.width));
                maxHeight = Math.max(maxHeight, Math.max(componentPreferredSize.height, componentMinimumSize.height));
                visibleCount++;
            }
        }

        int usedWidth = (alignment == TOP) ? maxWidth : maxWidth * visibleCount + gap * (visibleCount - 1);
        int usedHeight = (alignment == TOP) ? maxHeight * visibleCount + gap * (visibleCount - 1) : maxHeight;
        return new Dimension[]{
                new Dimension(maxWidth, maxHeight),
                new Dimension(usedWidth, usedHeight),
        };
    }

    public void layoutContainer(Container container) {
        Insets insets = container.getInsets();

        Component[] children = container.getComponents();
        Dimension dim[] = dimensions(children);

        int maxWidth = dim[0].width;
        int maxHeight = dim[0].height;
        int usedWidth = dim[1].width;
        //int usedHeight = dim[1].height;

        switch (alignment) {
            case TOP:
                for (int i = 0, c = children.length; i < c; i++) {
                    if (!children[i].isVisible())
                        continue;
                    children[i].setBounds(insets.left, insets.top + (maxHeight + gap) * i,
                            maxWidth, maxHeight);
                }
                break;
            case LEFT:
                for (int i = 0, c = children.length; i < c; i++) {
                    if (!children[i].isVisible())
                        continue;
                    children[i].setBounds(insets.left + (maxWidth + gap) * i, insets.top,
                            maxWidth, maxHeight);
                }
                break;
            case RIGHT:
            case BOTTOM:
                for (int i = 0, c = children.length; i < c; i++) {
                    if (!children[i].isVisible())
                        continue;
                    children[i].setBounds(container.getWidth() - insets.right - usedWidth + (maxWidth + gap) * i,
                            insets.top,
                            maxWidth, maxHeight);
                }
                break;
        }
    }

    public Dimension minimumLayoutSize(Container c) {
        return preferredLayoutSize(c);
    }

    public Dimension preferredLayoutSize(Container container) {
        Insets insets = container.getInsets();

        Component[] children = container.getComponents();
        Dimension dim[] = dimensions(children);

        int usedWidth = dim[1].width;
        int usedHeight = dim[1].height;

        return new Dimension(
                insets.left + usedWidth + insets.right,
                insets.top + usedHeight + insets.bottom);
    }

    public void addLayoutComponent(String string, Component comp) {
    }

    public void removeLayoutComponent(Component c) {
    }
}
