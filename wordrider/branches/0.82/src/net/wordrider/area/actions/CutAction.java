package net.wordrider.area.actions;

/**
 * @author Vity
 */

import net.wordrider.core.Lng;
import net.wordrider.utilities.Swinger;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 */
public final class CutAction extends DefaultEditorKit.CutAction {
    private static final CutAction instance = new CutAction();
    private static final String CODE = "CutAction";

    public static CutAction getInstance() {
        return instance;
    }

    private CutAction() {
        putValue(Action.NAME, Lng.getLabel(CODE));
        putValue(Action.SHORT_DESCRIPTION, Lng.getHint(CODE));
        putValue(Action.MNEMONIC_KEY, new Integer(Lng.getMnemonic(CODE)));
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
        putValue(Action.SMALL_ICON, Swinger.getIcon("cut.gif"));
    }

    public boolean isEnabled() {
        final JTextComponent comp = getFocusedComponent();
        return comp != null && comp.isEditable()
                && comp.isEnabled()
                && comp.getSelectedText() != null;
    }
}
