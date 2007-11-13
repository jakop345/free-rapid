package net.wordrider.area.actions;

import net.wordrider.area.RiderArea;
import net.wordrider.area.RiderDocument;
import net.wordrider.utilities.LogUtils;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public final class TrimTrailingSpacesAction extends StyledAreaAction {
    private static final TrimTrailingSpacesAction instance = new TrimTrailingSpacesAction();
    private final static Logger logger = Logger.getLogger(TrimTrailingSpacesAction.class.getName());

    public static TrimTrailingSpacesAction getInstance() {
        return instance;
    }

    private TrimTrailingSpacesAction() {
        super("TrimTrailingSpacesAction", KeyStroke.getKeyStroke(KeyEvent.VK_F3, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK), "ico_trim-sp.gif");
    }

    public final void actionPerformed(final ActionEvent e) {
        super.actionPerformed(e);
        final RiderArea src = getRiderArea(e);
        if (src == null)
            return;
        try {
            process(src);
        } catch (BadLocationException ex) {
            LogUtils.processException(logger, ex);
        }


    }

    private void process(RiderArea src) throws BadLocationException {
        final RiderDocument document = src.getDoc();
        final int selStart = src.getSelectionStart();
        final int selEnd = src.getSelectionEnd();
        final Element rootElement = document.getDefaultRootElement();

        final int startParaElement = rootElement.getElementIndex(selStart);
        final int endParaElement = rootElement.getElementIndex(selEnd);
      
        try {
            src.makeGroupChange(true);
            for (int i = startParaElement; i <= endParaElement; ++i) {
                final Element paraElement = rootElement.getElement(i);
                final int paraStart = paraElement.getStartOffset();
                String text = document.getText(paraStart, paraElement.getEndOffset() - paraStart);
                if (text.length() > 0) {
                    int j;
                    final char[] chars = text.toCharArray();
                    int temp = j = chars.length - 1 + (chars[chars.length - 1] == '\n' ? -1 : 0);
                    for (; j >= 0; --j) {
                        if (!Character.isWhitespace(chars[j]))
                            break;
                    }
                    temp -= j;
                    if (temp > 0) {
                        document.remove(paraStart + j + 1, temp);
                    }
                }
            }
        } finally {
            src.makeGroupChange(false);
        }
        //src.setCaretPosition(rootElement.getElement(endParaElement).getEndOffset());
    }


}
