package net.wordrider.area.actions;

import net.wordrider.area.RiderArea;
import net.wordrider.area.RiderDocument;
import net.wordrider.area.RiderStyles;
import net.wordrider.area.SelectedElementsIterator;
import net.wordrider.utilities.LogUtils;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Utilities;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public abstract class TextConversionAction extends StyledAreaAction {
    private final static Logger logger = Logger.getLogger(TextToUpperCaseAction.class.getName());

    public TextConversionAction(final String actionCode, final KeyStroke keyStroke, final String smallIcon) {
        super(actionCode, keyStroke, smallIcon);
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
        int realSelStart = src.getCaret().getMark();
        int realSelEnd = src.getCaret().getDot();
        final boolean isSelection = realSelStart != realSelEnd;
        final int selStart, selEnd;
        if (isSelection) {
            selStart = src.getSelectionStart();
            selEnd = src.getSelectionEnd();
        } else {
            final int wordEnd = Utilities.getWordEnd(src, realSelEnd);
            final int wordStart = Utilities.getWordStart(src, realSelEnd);
            if (realSelEnd >= wordStart && realSelEnd < wordEnd) {
                selStart = realSelStart = wordStart;
                selEnd = realSelEnd = wordEnd;
            } else
                return;
        }

        final List<SimpleStyledContent> list = findAndReplace(document, selStart, selEnd);
        if (list.isEmpty())
            return;
        doChanges(list, src, document);
        src.selectReverse(realSelStart, realSelEnd);
        document.refresh(selStart, selEnd);
    }

    List<SimpleStyledContent> findAndReplace(RiderDocument document, int selStart, int selEnd) throws BadLocationException {
        final Element rootElement = document.getDefaultRootElement();
        final SelectedElementsIterator eli = new SelectedElementsIterator(rootElement, selStart, selEnd, SelectedElementsIterator.FORWARD_DIRECTION);
        int eStart, eEnd, textStart, textLength;
        Element elem;

        final List<SimpleStyledContent> list = new LinkedList<SimpleStyledContent>();

        while ((elem = eli.next()) != null) {
            eStart = elem.getStartOffset();
            eEnd = elem.getEndOffset();
            if (RiderStyles.isText(elem)) {
                final AttributeSet attributes = elem.getAttributes();
                if (eli.isFirstLeafElement()) {
                    textStart = selStart;
                    textLength = (eli.isLastLeafElement()) ? selEnd - selStart : eEnd - selStart;
                } else {
                    textStart = eStart;
                    textLength = (eli.isLastLeafElement()) ? selEnd - eStart : eEnd - eStart;
                }
                final String text = document.getText(textStart, textLength);
                if (text.length() == 0 || (text.length() == 1) && text.equals("\n"))
                    continue;
                final String str = doStringConversion(text);
                if (!str.equals(text))
                    list.add(new SimpleStyledContent(str, textStart, textLength, attributes.copyAttributes(), elem.getParentElement().getAttributes().copyAttributes()));
            }
        }
        return list;
    }

    protected abstract String doStringConversion(String text);

    private void doChanges(List<SimpleStyledContent> list, RiderArea src, RiderDocument document) throws BadLocationException {
        try {
            src.makeGroupChange(true);
            for (SimpleStyledContent content : list) {
                document.remove(content.selStart, content.selLength);
                document.insertString(content.selStart, content.text, content.attributes);
                document.setParagraphAttributes(content.selStart, content.selLength, content.paraAttributes, true);
            }
        } finally {
            src.makeGroupChange(false);
        }
    }

    static class SimpleStyledContent {
         private AttributeSet attributes;
         private final AttributeSet paraAttributes;
         private String text;
         private int selStart;
         private int selLength;

         public SimpleStyledContent(String text, int selStart, int selLength, AttributeSet attributes, AttributeSet paraAttributes) {
             this.text = text;
             this.selStart = selStart;
             this.selLength = selLength;
             this.attributes = attributes;
             this.paraAttributes = paraAttributes;
         }
     }
}
