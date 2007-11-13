package net.wordrider.area;

import net.wordrider.core.swing.ColorPainterFactory;

import javax.swing.text.DefaultCaret;
import javax.swing.text.Highlighter;

/**
 * @author Vity
 */
class RiderCaret extends DefaultCaret {
    private static final Highlighter.HighlightPainter unfocusedPainter = ColorPainterFactory.createColorPainter(ColorStyles.COLOR_TEXT_NOTFOCUSED);
    // private static final Highlighter.HighlightPainter unfocusedPainter = new JaggedUnderlinePainter();

    private boolean isFocused;

    protected final Highlighter.HighlightPainter getSelectionPainter() {
        return isFocused ? super.getSelectionPainter()
                : unfocusedPainter;
    }

    public final void setSelectionVisible(final boolean hasFocus) {
        if (hasFocus != isFocused) {
            isFocused = hasFocus;
            super.setSelectionVisible(false);
            super.setSelectionVisible(true);
        }
    }

}