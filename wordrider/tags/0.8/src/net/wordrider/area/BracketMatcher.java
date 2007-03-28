package net.wordrider.area;


import net.wordrider.core.AppPrefs;
import net.wordrider.core.swing.ColorPainterFactory;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.*;


/**
 * A class to support highlighting of parenthesis.  To use it, add it as a caret listener to your text component.
 * <p/>
 * It listens for the location of the dot.  If the character before the dot is a close paren, it finds the matching
 * start1 paren and highlights both of them.  Otherwise it clears the highlighting.
 * <p/>
 * This object can be shared among multiple components.  It will only highlight one at a time.
 */
final class BracketMatcher {

    private static final String BRACKET_HIGHLIGHT_PROPERTY = "bracketMatcher";
    private static final String PAINTER_TAGS_PROPERTY = "bracketMatcherTags";


    /**
     * Used to paint good parenthesis matches
     */
    private final static Highlighter.HighlightPainter goodPainter = ColorPainterFactory.createColorPainter(ColorStyles.COLOR_BRACKET_MATCH);

    /**
     * Used to paint bad parenthesis matches
     */
    private final static Highlighter.HighlightPainter badPainter = ColorPainterFactory.createColorPainter(ColorStyles.COLOR_BRACKET_MISMATCH);


    private BracketMatcher() {
// static util class only
    }

    private static final CaretListener caretListener = new CaretListener() {
        public void caretUpdate(CaretEvent e) {
            BracketMatcher.caretUpdate(e);
        }
    };

// Installs CurrentLineHilighter for the given JTextComponent

    public static void install(final JTextComponent c) {
        if (c.getClientProperty(BRACKET_HIGHLIGHT_PROPERTY) == null) {
            c.putClientProperty(BRACKET_HIGHLIGHT_PROPERTY, BRACKET_HIGHLIGHT_PROPERTY);
            c.putClientProperty(PAINTER_TAGS_PROPERTY, new PainterTags());
            c.addCaretListener(caretListener);
        }
    }

// Uninstalls CurrentLineHighligher for the given JTextComponent

    public static void uninstall(final JTextComponent c) {
        if (c.getClientProperty(BRACKET_HIGHLIGHT_PROPERTY) != null) {
            final PainterTags tags = (PainterTags) c.getClientProperty(PAINTER_TAGS_PROPERTY);
            tags.clearHighlights();
            c.putClientProperty(BRACKET_HIGHLIGHT_PROPERTY, null);
            c.putClientProperty(PAINTER_TAGS_PROPERTY, null);
            c.removeCaretListener(caretListener);
        }
    }


    /**
     * Returns the character at position p in the document
     */
    private static char getCharAt(final Document doc, final int p)
            throws BadLocationException {
        return doc.getText(p, 1).charAt(0);
    }

    /**
     * Returns the position of the matching parenthesis (bracket, whatever) for the character at paren.  It counts all
     * kinds of brackets, so the "matching" parenthesis might be a bad one.  For this demo, we're not going to take
     * quotes or comments into account since that's not the point.
     * <p/>
     * It's assumed that paren is the position of some parenthesis character
     * @return the position of the matching paren, or -1 if none is found
     */
    private static int findMatchingParenBack(final Document d, final int paren, final int limit)
            throws BadLocationException {
        int parenCount = 1;
        int i = paren - 1;
        for (; i >= limit; i--) {
            char c = getCharAt(d, i);
            switch (c) {
                case')':
                case'}':
                case']':
                    parenCount++;
                    break;
                case'(':
                case'{':
                case'[':
                    parenCount--;
                    break;
            }
            if (parenCount == 0)
                break;
        }
        return i;
    }

    private static int findMatchingParenForward(final Document d, final int paren, final int limit)
            throws BadLocationException {
        int parenCount = 1;
        int i = paren + 1;
        for (; i < limit; i++) {
            char c = getCharAt(d, i);
            switch (c) {
                case')':
                case'}':
                case']':
                    --parenCount;
                    break;
                case'(':
                case'{':
                case'[':
                    ++parenCount;
                    break;
            }
            if (parenCount == 0)
                break;
        }
        return i;
    }

    private static final class PainterTags {
        /**
         * The last highlighter used
         */
        Highlighter highlighter = null;

        /**
         * The tags returned from the highlighter, used for clearing the current highlight.
         */
        Object start1 = null,
                end1 = null,
                start2 = null,
                end2 = null;

        private void clearHighlights() {
            if (highlighter != null) {
                if (start1 != null)
                    highlighter.removeHighlight(start1);
                if (end1 != null)
                    highlighter.removeHighlight(end1);
                if (start2 != null)
                    highlighter.removeHighlight(start2);
                if (end2 != null)
                    highlighter.removeHighlight(end2);
                start2 = end2 = start1 = end1 = null;
                highlighter = null;
            }
        }

        private Object addHighlighter(int closeParen, Highlighter.HighlightPainter painter) throws BadLocationException {
            return highlighter.addHighlight(closeParen,
                    closeParen + 1,
                    painter);
        }
    }

    /**
     * Called whenever the caret moves, it updates the highlights
     */
    private static void caretUpdate(final CaretEvent e) {
        final JTextComponent source = (JTextComponent) e.getSource();
        final PainterTags tags = (PainterTags) source.getClientProperty(PAINTER_TAGS_PROPERTY);
        tags.clearHighlights();
        final int caretPosition = e.getDot();
        final int leftLimit, rightLimit;
        final Document doc = source.getDocument();
        if (AppPrefs.getProperty(AppPrefs.MATCH_BRACKET_MATHONLY, true)) {
            final Element paraElement = Utilities.getParagraphElement(source, caretPosition);
            if (RiderStyles.isMath(paraElement)) {
                leftLimit = paraElement.getStartOffset();
                rightLimit = paraElement.getEndOffset();
            } else return;
        } else {
            leftLimit = 0;
            rightLimit = doc.getLength();
        }
        tags.highlighter = source.getHighlighter();

        if (caretPosition != leftLimit) {
            // The character we want is the one before the current position
            int closeParen = caretPosition - 1;
            try {
                char c = getCharAt(doc, closeParen);
                if (c == ')' || c == ']' || c == '}') {
                    int openParen = findMatchingParenBack(doc, closeParen, leftLimit);
                    if (openParen >= leftLimit) {
                        final char c2 = getCharAt(doc, openParen);
                        final Highlighter.HighlightPainter painter = ((c2 == '(' && c == ')') ||
                                (c2 == '{' && c == '}') ||
                                (c2 == '[' && c == ']')) ? goodPainter : badPainter;
                        tags.start1 = tags.addHighlighter(openParen, painter);
                        tags.end1 = tags.addHighlighter(closeParen, painter);
                    } else {
                        tags.end1 = tags.addHighlighter(closeParen, badPainter);
                    }

                }
            }
            catch (BadLocationException ignore) {
                //LogUtils.processException(logger, ex);
            }
        }

        if (caretPosition < rightLimit) {
            try {
                char c = getCharAt(doc, caretPosition);
                if (c == '(' || c == '[' || c == '{') {
                    int closeParen = findMatchingParenForward(doc, caretPosition, rightLimit);
                    if (closeParen < rightLimit) {
                        final char c2 = getCharAt(doc, closeParen);
                        final Highlighter.HighlightPainter painter = ((c == '(' && c2 == ')') ||
                                (c == '{' && c2 == '}') ||
                                (c == '[' && c2 == ']')) ? goodPainter : badPainter;
                        tags.start2 = tags.addHighlighter(caretPosition, painter);
                        tags.end2 = tags.addHighlighter(closeParen, painter);
                    } else {
                        tags.end2 = tags.addHighlighter(caretPosition, badPainter);
                    }

                }
            }
            catch (BadLocationException ignore) {
                //LogUtils.processException(logger, ex);
            }
        }
    }

}
