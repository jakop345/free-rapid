package net.wordrider.area.actions;

import net.wordrider.area.RiderEditorKit;
import net.wordrider.utilities.LogUtils;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Utilities;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public final class SelectWordAction extends WordAction {
    private final static Logger logger = Logger.getLogger(SelectWordAction.class.getName());

    public SelectWordAction() {
        super("SelectWordAction");
    }

    /**
     * Returns the character at position p in the document
     */
    private static char getCharAt(final Document doc, final int p) {
        try {
            return doc.getText(p, 1).charAt(0);
        } catch (BadLocationException ex) {
            return 0;
        }
    }

    private int getEndOfWord(final Element el, final Document doc, final int caretPosition) {
        try {
            final String line = doc.getText(caretPosition, el.getEndOffset() - caretPosition);
            int i;
            final int textLength = line.length();
            for (i = 0; i < textLength; ++i)
                if (RiderEditorKit.WORD_SEPARATORS.get(line.charAt(i)))
                    break;
            return i + caretPosition;
        } catch (BadLocationException e) {
            LogUtils.processException(logger, e);
            return el.getEndOffset();
        }
    }

    private int getBeginOfWord(final Element el, final Document doc, final int caretPosition) {
        try {
            final String line = doc.getText(el.getStartOffset(), caretPosition - el.getStartOffset());
            int i;
            for (i = line.length() - 1; i >= 0; --i)
                if (RiderEditorKit.WORD_SEPARATORS.get(line.charAt(i)))
                    break;
            return el.getStartOffset() + i + 1;
        } catch (BadLocationException e) {
            LogUtils.processException(logger, e);
            return el.getStartOffset();
        }
    }

    private int checkWord(final Element el, final Document doc, final int startPosition, final int endPosition) {
        try {
            final String line = doc.getText(startPosition, endPosition - startPosition);
            int i;
            final int textLength = line.length();
            for (i = 0; i < textLength; ++i)
                if (RiderEditorKit.WORD_SEPARATORS.get(line.charAt(i)))
                    break;
            return i + startPosition;
        } catch (BadLocationException e) {
            LogUtils.processException(logger, e);
            return el.getEndOffset();
        }
    }


    public void actionPerformed(final ActionEvent evt) {
        super.actionPerformed(evt);
        Document doc = textArea.getDocument();
        final int selectionStart = textArea.getSelectionStart();
        final int selectionEnd = textArea.getSelectionEnd();
        final Element line = Utilities.getParagraphElement(textArea, caretPosition);
        final int endOffset = line.getEndOffset();
        final int startOffset = line.getStartOffset();
        if (selectionStart == selectionEnd) {
            boolean resultRight = false;
            boolean resultLeft = false;
            if (caretPosition > startOffset) {
                final char ch = getCharAt(doc, caretPosition - 1);
                resultLeft = ch != 0 && !RiderEditorKit.WORD_SEPARATORS.get(ch);
            }
            if (caretPosition < endOffset) {
                final char ch = getCharAt(doc, caretPosition);
                resultRight = ch != 0 && !RiderEditorKit.WORD_SEPARATORS.get(ch);
            }
            if (resultLeft && resultRight) {
                textArea.select(getWordInDirection(RiderEditorKit.DIRECTION_PREVIOUS), getEndOfWord(line, doc, caretPosition));
            } else if (resultLeft) {
                textArea.moveCaretPosition(getWordInDirection(RiderEditorKit.DIRECTION_PREVIOUS));
            } else if (resultRight) {
                textArea.moveCaretPosition(getEndOfWord(line, doc, caretPosition));
            } else textArea.select(startOffset, endOffset - 1);
        } else {
            if (selectionStart >= startOffset && selectionEnd <= endOffset) {
                if (!(selectionStart == startOffset && selectionEnd == endOffset - 1)) {
                    if (checkWord(line, doc, selectionStart, selectionEnd) != selectionEnd)
                        textArea.select(startOffset, endOffset - 1);
                    else {
                        final int beginWord = getBeginOfWord(line, doc, selectionStart);
                        final int endWord = getEndOfWord(line, doc, selectionEnd);
                        if (beginWord == selectionStart && endWord == selectionEnd)
                            textArea.select(startOffset, endOffset - 1);
                        else
                            textArea.select(beginWord, endWord);
                    }
                    return;
                }
            }
            textArea.selectAll();
        }

    }

}
