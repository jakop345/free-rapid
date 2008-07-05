package cz.cvut.felk.erm.swing.painters;

import javax.swing.text.DefaultCaret;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.FocusEvent;

/**
 * @author Ladislav Vitasek
 */
public class RiderCaret extends DefaultCaret {
    private static final Highlighter.HighlightPainter unfocusedPainter = ColorPainterFactory.createColorPainter(new Color(230, 230, 210));

    private boolean isFocused;


    public RiderCaret() {
        super();
        setVisible(true);
        setUpdatePolicy(ALWAYS_UPDATE);
        setBlinkRate(500);
    }

    protected final Highlighter.HighlightPainter getSelectionPainter() {
        return isFocused ? super.getSelectionPainter()
                : unfocusedPainter;
    }

//    @Override
//    public int getBlinkRate() {
//        return 300;
//    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void focusGained(FocusEvent e) {
        setVisible(true);
        setSelectionVisible(true);
    }

//    @Override
//    public boolean isVisible() {
//        return new Random().nextBoolean();
//    }


    public final void setSelectionVisible(final boolean hasFocus) {
        if (hasFocus != isFocused) {
            isFocused = hasFocus;
            super.setSelectionVisible(false);
            super.setSelectionVisible(true);
        }
    }

}
