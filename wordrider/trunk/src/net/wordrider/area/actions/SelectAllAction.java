package net.wordrider.area.actions;

import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * @author Vity
 */
public final class SelectAllAction extends TextAreaAction {
    private final static SelectAllAction instance = new SelectAllAction();
    private final static String CODE = "SelectAllAction";

    private SelectAllAction() {
        super(CODE, KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK), null);
    }

    public static SelectAllAction getInstance() {
        return instance;
    }

    /**
     * The operation to perform when this action is triggered.
     */
    public final void actionPerformed(final ActionEvent e) {
        final JTextComponent target = getTextComponent(e);
        if (target != null) {
            final Document doc = target.getDocument();
            target.setCaretPosition(0);
            target.moveCaretPosition(doc.getLength());
        }
    }

    public boolean isEnabled() {
        final JTextComponent comp = getFocusedComponent();
        return comp != null && comp.isEnabled()
                && comp.getText().length() > 0;
    }

}
