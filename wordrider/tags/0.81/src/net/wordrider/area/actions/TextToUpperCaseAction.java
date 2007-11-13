package net.wordrider.area.actions;

import javax.swing.*;

/**
 * @author Vity
 */
public final class TextToUpperCaseAction extends TextConversionAction {
    private static final TextConversionAction instance = new TextToUpperCaseAction();

    public static TextConversionAction getInstance() {
        return instance;
    }

    private TextToUpperCaseAction() {
        super("TextToUpperCaseAction", KeyStroke.getKeyStroke("F3"), "ico_upper-c.gif");
    }


    protected String doStringConversion(final String text) {
        return text.toUpperCase();
    }


}
