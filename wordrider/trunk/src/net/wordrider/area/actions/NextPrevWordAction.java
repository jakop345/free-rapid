package net.wordrider.area.actions;

import java.awt.event.ActionEvent;

/**
 * @author Vity
 */
public final class NextPrevWordAction extends WordAction {
    private final boolean select;
    private final int direction;

    public NextPrevWordAction(final String action, final boolean select, final int direction) {
        super(action);
        this.select = select;
        this.direction = direction;
    }

    public void actionPerformed(final ActionEvent evt) {
        super.actionPerformed(evt);
        caretPosition = getWordInDirection(direction);
        if (caretPosition != NO_WORD_AVAILABLE) {
            if (select)
                getTextArea().moveCaretPosition(caretPosition);
            else
                getTextArea().setCaretPosition(caretPosition);
        }
    }
}
