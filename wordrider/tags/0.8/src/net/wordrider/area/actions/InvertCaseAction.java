package net.wordrider.area.actions;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * @author Vity
 */
public final class InvertCaseAction extends TextConversionAction {
    private static final TextConversionAction instance = new InvertCaseAction();

    public static TextConversionAction getInstance() {
        return instance;
    }

    private InvertCaseAction() {
        super("InvertCaseAction", KeyStroke.getKeyStroke(KeyEvent.VK_F3, InputEvent.SHIFT_MASK), "ico_inv-case.gif");
    }

    protected String doStringConversion(final String text) {
        final char[] chars = text.toCharArray();
        final int length = chars.length;
        char c;
        for (int i = 0; i < length; ++i) {
            c = chars[i];
            if (Character.isLowerCase(c))
                chars[i] = Character.toUpperCase(c);
            else if (Character.isUpperCase(c))
                chars[i] = Character.toLowerCase(c);
        }
        return new String(chars);
    }


}
