package net.wordrider.area.actions;

import net.wordrider.area.RiderDocument;
import net.wordrider.area.RiderEditorKit;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Vity
 */
public final class CapitalizeAction extends TextConversionAction {
    private static final CapitalizeAction instance = new CapitalizeAction();

    public static CapitalizeAction getInstance() {
        return instance;
    }

    private CapitalizeAction() {
        super("CapitalizeAction", KeyStroke.getKeyStroke(KeyEvent.VK_F3, InputEvent.CTRL_MASK), "ico_capit.gif");
    }


    protected String doStringConversion(String text) {
        return null;
    }

    @Override
    protected List<SimpleStyledContent> findAndReplace(RiderDocument document, int selStart, int selEnd) throws BadLocationException {
        final Element rootElement = document.getDefaultRootElement();
        final int startParaElement = rootElement.getElementIndex(selStart);
        final int endParaElement = rootElement.getElementIndex(selEnd);

        final List<TextConversionAction.SimpleStyledContent> list = new LinkedList<TextConversionAction.SimpleStyledContent>();
        for (int i = startParaElement; i <= endParaElement; ++i) {
            final Element paraElement = rootElement.getElement(i);
            final int paraStart = Math.max(paraElement.getStartOffset(), selStart);
            final int paraEnd = Math.min(paraElement.getEndOffset(), selEnd);
            final String text = document.getText(paraStart, paraEnd - paraStart);
            if (text.length() > 0) {
                final AttributeSet paraAttributes = paraElement.getAttributes().copyAttributes();
                final char[] chars = text.toCharArray();
                boolean wasWhite = true;
                final int length = chars.length;
                char c;
                for (int j = 0; j < length; ++j) {
                    c = chars[j];
                    if (wasWhite) {
                        if (!RiderEditorKit.WORD_SEPARATORS.get(c)) {
                            wasWhite = false;
                            char upper = Character.toTitleCase(chars[j]);
                            if (upper != c) {
                                final int offset = paraStart + j;
                                final Element element = paraElement.getElement(paraElement.getElementIndex(offset));
                                list.add(new SimpleStyledContent(String.valueOf(upper), offset, 1, element.getAttributes().copyAttributes(), paraAttributes));
                            }
                        }
                    } else wasWhite = RiderEditorKit.WORD_SEPARATORS.get(c);
                }
            }
        }
        return list;
    }
}
