package cz.cvut.felk.erm.swing.painters;

import javax.swing.text.DefaultHighlighter;
import java.awt.*;

/**
 * @author Ladislav Vitasek
 */
class ColorHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
    private final Color color;

    public ColorHighlightPainter(final Color color) {
        super(null);    //call to super
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
