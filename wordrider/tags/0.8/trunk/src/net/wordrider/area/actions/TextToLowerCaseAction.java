package net.wordrider.area.actions;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * @author Vity
 */
public final class TextToLowerCaseAction extends TextConversionAction {
    private static final TextConversionAction instance = new TextToLowerCaseAction();

    public static TextConversionAction getInstance() {
        return instance;
    }

    private TextToLowerCaseAction() {
        super("TextToLowerCaseAction", KeyStroke.getKeyStroke(KeyEvent.VK_F3, InputEvent.ALT_MASK), "ico_lower-c.gif");
    }

    protected String doStringConversion(final String text) {
        return text.toLowerCase();
    }


}
