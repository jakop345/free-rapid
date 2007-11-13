package net.wordrider.area.actions;

import net.wordrider.area.RiderArea;
import net.wordrider.area.RiderDocument;
import net.wordrider.utilities.Consts;
import net.wordrider.utilities.LogUtils;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

/**
 * @author Vity
 */
abstract class InsertSeparateLineAction extends TextAreaAction {
    private final int lineType;
    private final static Logger logger = Logger.getLogger(InsertSeparateLineAction.class.getName());
    protected InsertSeparateLineAction(final int lineType, final String actionCode, final KeyStroke keyStroke, final String smallIcon) {
        super(actionCode, keyStroke, smallIcon);
        this.lineType = lineType;
    }


    public final void actionPerformed(final ActionEvent e) {
        final RiderArea area = (RiderArea) getTextComponent(e);
        if (area != null) {
            try {
                int caretPosition = area.getCaretPosition();
                final RiderDocument doc = area.getDoc();
                final AttributeSet inputSet = area.getInputAttributes().copyAttributes();
                area.replaceSelection("");
                final boolean rowstart = Utilities.getRowStart(area, caretPosition) == caretPosition;
                if (!rowstart)
                    doc.insertString(caretPosition++, Consts.LINE_SEPARATOR, area.getInputAttributes());
                doc.insertSeparateLine(area, caretPosition++, lineType);
                doc.insertString(caretPosition, Consts.LINE_SEPARATOR, inputSet);
            } catch (BadLocationException ex) {
                LogUtils.processException(logger, ex);
            }
        }
    }

}
