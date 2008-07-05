package cz.cvut.felk.erm.swing.painters;

import javax.swing.text.Highlighter;
import java.awt.*;

/**
 * @author Ladislav Vitasek
 */
public class ColorPainterFactory {
    private ColorPainterFactory() {
    }

    public static Highlighter.HighlightPainter createColorPainter(final Color color) {
        return new ColorHighlightPainter(color);
    }

}