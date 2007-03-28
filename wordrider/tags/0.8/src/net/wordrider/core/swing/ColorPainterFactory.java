package net.wordrider.core.swing;

import javax.swing.text.Highlighter;

/**
 * @author Vity
 */
public class ColorPainterFactory {
    private ColorPainterFactory() {
    }

    public static Highlighter.HighlightPainter createColorPainter(final int colorCode) {
        return new ColorHighlightPainter(colorCode);
    }

    public static Highlighter.HighlightPainter createJaggedUnderlinePainter(final int colorCode) {
        return new JaggedUnderlinePainter(colorCode);
    }
}
