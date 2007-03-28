package net.wordrider.core.managers;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * @author Vity
 */
class VerticalTextIcon implements Icon, SwingConstants {
    private Font font = UIManager.getFont("TabbedPane.font");
    // private Font font = UIManager.getFont("Label.font");
    private FontMetrics fm = new JLabel().getFontMetrics(font);

    private final String text;
    private int width, height;
    private final boolean clockwize;
    private final Icon icon;

    public VerticalTextIcon(final String text, final Icon icon, final boolean clockwize) {
        this.text = text;
        width = fm.stringWidth(text) + 3 + icon.getIconWidth();
        height = Math.max(fm.getHeight(), icon.getIconHeight());
        this.clockwize = clockwize;
        this.icon = icon;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g;
        Font oldFont = g.getFont();
        Color oldColor = g.getColor();
        AffineTransform oldTransform = g2.getTransform();

        g.setFont(font);
        g.setColor(Color.black);
        if (clockwize) {
            g2.translate(x + getIconWidth(), y);
            g2.rotate(Math.PI / 2);
        } else {
            g2.translate(x, y + getIconHeight());
            g2.rotate(-Math.PI / 2);
        }
        g.drawString(text, 3 + icon.getIconWidth(), fm.getLeading() + fm.getAscent());
        icon.paintIcon(c, g, 0, 2);
        g.setFont(oldFont);
        g.setColor(oldColor);
        g2.setTransform(oldTransform);
    }

    public int getIconWidth() {
        return height;
    }

    public int getIconHeight() {
        return width;
    }
}
