package net.wordrider.core.swing;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import javax.swing.text.View;
import java.awt.*;

/**
 * @author Vity
 */
class JaggedUnderlinePainter extends ColorHighlightPainter {
    public JaggedUnderlinePainter(final int colorCode) {
        super(colorCode);
    }

    public Shape paintLayer(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c, View view) {
        Color color = getColor();
        Color oldColor = g.getColor();
        g.setColor(color);

        if (offs0 == view.getStartOffset() &&
                offs1 == view.getEndOffset()) {
            Rectangle alloc;
            if (bounds instanceof Rectangle) {
                alloc = (Rectangle) bounds;
            } else {
                alloc = bounds.getBounds();
            }
            drawJaggedLine(alloc, g);
            return alloc;
        } else {
            try {
                Shape shape = view.modelToView(offs0, Position.Bias.Forward,
                        offs1, Position.Bias.Backward,
                        bounds);
                Rectangle r = (shape instanceof Rectangle) ?
                        (Rectangle) shape : shape.getBounds();
                drawJaggedLine(r, g);
                return r;
            } catch (BadLocationException e) {
                //
            }
        }
        g.setColor(oldColor);
        return null;
    }

    private void drawJaggedLine(Rectangle rect, Graphics g) {
        int y = rect.y + rect.height;
        int x1 = rect.x;
        int x2 = x1 + rect.width - 6;
        for (int i = x1 - 6; i <= x2; i += 6) {
            g.drawArc(i + 3, y - 3, 3, 3, 0, 180);
            g.drawArc(i + 6, y - 3, 3, 3, 180, 181);
        }
    }
}