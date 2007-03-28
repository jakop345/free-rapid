package net.wordrider.gui;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

@SuppressWarnings({"WeakerAccess"})
public final class ToolbarSeparatorUI extends javax.swing.plaf.basic.BasicToolBarSeparatorUI {
    private ToolbarSeparatorUI() {
        shadow = UIManager.getColor("controlDkShadow");
        highlight = UIManager.getColor("controlLtHighlight");
    }

    public static ComponentUI createUI(JComponent c) {
        c = null;//must be
        return new ToolbarSeparatorUI();
    }

    public final void paint(final Graphics g, final JComponent c) {
        final Dimension s = c.getSize();
        final int sWidth = s.width / 2;

        g.setColor(shadow);
        g.drawLine(sWidth, 0, sWidth, s.height);

        g.setColor(highlight);
        g.drawLine(sWidth + 1, 0, sWidth + 1, s.height);
    }
}

