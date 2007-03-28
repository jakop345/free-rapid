package net.wordrider.area.actions;

import net.wordrider.area.RiderEditorKit;
import net.wordrider.utilities.LogUtils;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.Utilities;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

/**
 * @author Vity
 */
abstract class WordAction extends AbstractAction {
    int caretPosition;
    JTextComponent textArea = null;
    private final static Logger logger = Logger.getLogger(WordAction.class.getName());
    static final int NO_WORD_AVAILABLE = -100;

    protected WordAction(final String actionName) {
        super(actionName);    //call to super
    }


    public void actionPerformed(final ActionEvent evt) {
        textArea = (JTextComponent) evt.getSource();
        caretPosition = textArea.getCaretPosition();
    }

    final JTextComponent getTextArea() {
        return textArea;
    }

    final int getWordInDirection(final int direction) {
        if (textArea != null) {
            final Element line = Utilities.getParagraphElement(textArea, caretPosition);
            final int lineStart = line.getStartOffset();
            final String lineText;
            try {
                lineText = line.getDocument().getText(lineStart, line.getEndOffset() - lineStart);
            } catch (BadLocationException e) {
                LogUtils.processException(logger, e);
                return NO_WORD_AVAILABLE;
            }
            return (direction == RiderEditorKit.DIRECTION_PREVIOUS) ? getPrevWord(textArea, caretPosition, lineStart, lineText) : getNextWord(textArea, caretPosition, lineStart, lineText);
        } else return NO_WORD_AVAILABLE;
    }

    /*
       Returns absolute position of the caretPosition at the line for previous word
       @return NO_WORD_AVAILABLE if no word available
    */
    private static int getPrevWord(final JTextComponent textArea, int caret, final int lineStart, final String lineText) {
        caret -= lineStart;

        if (caret <= 0) {
            if (lineStart == 0) {
                textArea.getToolkit().beep();
                return NO_WORD_AVAILABLE;
            }
            return lineStart + --caret;
        } else {
            char ch;
            --caret;
            int state = 1;
            for (; caret >= 0; --caret) {
                ch = lineText.charAt(caret);
                switch (state) {
                    case 1:
                        if (ch == ' ')
                            state = 1;
                        else if (isWordSeparator(ch))
                            state = 5;
                        else
                            state = 4;
                        break;
                    case 4:
                        if (RiderEditorKit.WORD_SEPARATORS.get(ch))
                            state = 3;
                        else
                            state = 4;
                        break;
                    case 5:
                        if (isWordSeparator(ch))
                            state = 5;
                        else
                            state = 3;
                        break;
                    case 3:
                        break;
                    default:
                        break;
                }
                if (state == 3)
                    break;
            }
            return lineStart + ++caret;
        }
    }


    /*
       Returns absolute position of the caretPosition at the line for next word
       @return NO_WORD_AVAILABLE if no word available
    */
    private static int getNextWord(final JTextComponent textArea, int caret, final int lineStart, final String lineText) {
        caret -= lineStart;

        final int length = lineText.length();
        if (caret + 1 >= length) {
            if (lineStart + caret == textArea.getDocument().getLength()) {
                textArea.getToolkit().beep();
                return NO_WORD_AVAILABLE;
            }
            return lineStart + ++caret;
        } else {
            char ch;
            int state = 1;
            for (; caret < length - 1; ++caret) {
                ch = lineText.charAt(caret);
                switch (state) {
                    case 1:
                        if (ch == ' ')
                            state = 2;
                        else if (isWordSeparator(ch))
                            state = 5;
                        else
                            state = 4;
                        break;
                    case 2:
                        if (ch == ' ')
                            state = 2;
                        else
                            state = 3;
                        break;
                    case 4:
                        if (ch == ' ')
                            state = 2;
                        else if (isWordSeparator(ch))
                            state = 3;
                        else
                            state = 4;
                        break;
                    case 5:
                        if (isWordSeparator(ch))
                            state = 5;
                        else if (ch == ' ')
                            state = 2;
                        else
                            state = 3;
                        break;
                    case 3:
                        break;
                    default:
                        break;
                }
                if (state == 3) break;
            }
            return lineStart + caret;
        }
    }

    private static boolean isWordSeparator(final char ch) {
        return ch != ' ' && RiderEditorKit.WORD_SEPARATORS.get(ch);
    }
}
