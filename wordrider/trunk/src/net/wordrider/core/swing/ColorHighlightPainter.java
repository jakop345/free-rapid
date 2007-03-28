package net.wordrider.core.swing;

import net.wordrider.area.ColorStyles;

import javax.swing.text.DefaultHighlighter;
import java.awt.*;

/**
 * User: Vity
 */
class ColorHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
    private final int colorCode;

    public ColorHighlightPainter(final int colorCode) {
        super(null);    //call to super
        this.colorCode = colorCode;
    }

    public Color getColor() {
        return ColorStyles.getColor(colorCode);
    }
}
