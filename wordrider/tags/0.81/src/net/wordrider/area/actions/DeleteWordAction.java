package net.wordrider.area.actions;

import net.wordrider.utilities.LogUtils;

import javax.swing.text.BadLocationException;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public final class DeleteWordAction extends WordAction {
    private final int direction;
    private final static Logger logger = Logger.getLogger(DeleteWordAction.class.getName());

    public DeleteWordAction(final int direction) {
        super("DeleteWordAction");
        this.direction = direction;
    }

    public final void actionPerformed(final ActionEvent e) {
        super.actionPerformed(e);
        int selStart = textArea.getSelectionStart();
        int selEnd = textArea.getSelectionEnd();
        if (selStart == selEnd) {
            selStart = caretPosition;
            caretPosition = getWordInDirection(direction);
            if (caretPosition != NO_WORD_AVAILABLE) {
                selEnd = caretPosition - selStart;
                if (selEnd < 0) {  //direction left
                    selEnd = Math.abs(selEnd);
                    selStart = caretPosition;
                }
            } else return;
        } else selEnd -= selStart;
        try {
            textArea.getDocument().remove(selStart, selEnd);
        } catch (BadLocationException ex) {
            logger.severe("caretPosition : " + caretPosition);
            LogUtils.processException(logger, ex);
        }
    }

}
