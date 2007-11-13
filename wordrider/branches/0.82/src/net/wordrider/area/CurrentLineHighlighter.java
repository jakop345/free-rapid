package net.wordrider.area;

import net.wordrider.utilities.LogUtils;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Logger;

/**
 * This class can be used to highlight the current line for any JTextComponent.
 * @author Santhosh Kumar T
 * @author Peter De Bruycker
 * @author Vity
 * @version 1.2
 */
final class CurrentLineHighlighter {
    private static final String LINE_HIGHLIGHT = "linehighlight";
    private static final String PREVIOUS_CARET = "previousCaret";
    private static final String UNDO_KEY = "makingundo";
    private final static Logger logger = Logger.getLogger(CurrentLineHighlighter.class.getName());

    private CurrentLineHighlighter() {
// static util class only
    }


    public static void install(final JTextComponent c) {
        if (c.getClientProperty(LINE_HIGHLIGHT) != null)
            return;
        try {
            final Object obj = c.getHighlighter().addHighlight(0, 0, painter);
            c.putClientProperty(LINE_HIGHLIGHT, obj);
            c.putClientProperty(PREVIOUS_CARET, c.getCaretPosition());
            c.addCaretListener(caretListener);
            c.addMouseListener(mouseListener);
            c.addMouseMotionListener(mouseMotionListener);
        }
        catch (BadLocationException ex) {
            LogUtils.processException(logger, ex);
        }


    }

// Uninstalls CurrentLineHighligher for the given JTextComponent

    public static void uninstall(final JTextComponent c) {
        final Object lineHighlightProperty = c.getClientProperty(LINE_HIGHLIGHT);
        if (lineHighlightProperty != null) {
            c.getHighlighter().removeHighlight(lineHighlightProperty);
            c.putClientProperty(LINE_HIGHLIGHT, null);
            c.putClientProperty(PREVIOUS_CARET, null);
            c.removeCaretListener(caretListener);
            c.removeMouseListener(mouseListener);
            c.removeMouseMotionListener(mouseMotionListener);
        }
    }

    private static final CaretListener caretListener = new CaretListener() {

        public void caretUpdate(final CaretEvent e) {
            final JTextComponent c = (JTextComponent) e.getSource();
            final Object makingUndo = c.getClientProperty(UNDO_KEY);
            if (makingUndo != null)
                return;
            final RiderArea riderArea = (RiderArea) c;
            if (riderArea.isBusy())
                return;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    CurrentLineHighlighter.caretUpdate(c);
                }
            });

        }
    };

    private static final MouseMotionListener mouseMotionListener = new MouseMotionAdapter() {
        public void mouseDragged(MouseEvent e) {
            JTextComponent c = (JTextComponent) e.getSource();
            caretUpdate(c);
        }
    };


    private static final MouseListener mouseListener = new MouseAdapter() {
// highlight the line the user clicks on

        public void mousePressed(MouseEvent e) {
            JTextComponent c = (JTextComponent) e.getSource();
            caretUpdate(c);
        }
    };


    public static void updateDnD(final JTextComponent c) {
        if (c.getClientProperty(LINE_HIGHLIGHT) != null)
            caretUpdate(c);
    }

    /**
     * Fetches the previous caret location, stores the current caret location, If the caret is on another line, repaint
     * the previous line and the current line
     * @param c the text component
     */
    private static void caretUpdate(final JTextComponent c) {
        try {
            final int previousCaret = (Integer) c.getClientProperty(PREVIOUS_CARET);

            final int actualCaretPosition = c.getCaretPosition();
//            System.out.println("Previous caret:" + previousCaret);
            //final Document doc = c.getDocument();
            //final Element el = doc.getDefaultRootElement();
            //final boolean isReadonlySection = RiderStyles.isReadonlySection(el.getElement(el.getElementIndex(actualCaretPosition)));
            //if (!isReadonlySection)
            c.putClientProperty(PREVIOUS_CARET, actualCaretPosition);
            if (previousCaret <= c.getDocument().getLength()) {
                final Rectangle prev = c.modelToView(previousCaret);
//            System.out.println("Current caret:" + actualCaretPosition);
                final Rectangle r = c.modelToView(actualCaretPosition);
//            System.out.println("ALL OK");
                if (prev.y != r.y) {
                    c.repaint(0, prev.y, c.getWidth(), r.height + 4);
                    c.repaint(0, r.y, c.getWidth(), r.height);
                }
            }
        } catch (Exception ignore) {
            // LogUtils.processException(logger, ignore);
        }
    }

    private static final Highlighter.HighlightPainter painter = new Highlighter.HighlightPainter() {
        public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {
            final Document doc = c.getDocument();
            final Element el = doc.getDefaultRootElement();
            final int caretPosition = c.getCaretPosition();
            if (RiderStyles.isReadonlySection(el.getElement(el.getElementIndex(caretPosition))))
                return;

            try {
                final Rectangle r = c.modelToView(caretPosition);
                if (r == null)
                    return;
                g.setColor(ColorStyles.getColor(ColorStyles.COLOR_HIGHLIGHT_LINE));
                //  g.setXORMode(Color.WHITE);
                g.fillRect(RiderArea.LEFT_BORDER_WIDTH, r.y, c.getWidth(), r.height);
//                System.out.println("r.x:" + r.x);
//                System.out.println("r.y:" + r.y);
//                System.out.println("r.width:" + r.width);
//                System.out.println("r.height:" + r.height);
//                g.fillRect(r.x, r.y, r.width, r.height);
                //g.setPaintMode();
            }
            catch (BadLocationException ignore) {
                //   LogUtils.processException(logger, ignore);
            }
        }
    };
}