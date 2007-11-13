package net.wordrider.area.actions;

import net.wordrider.core.Lng;
import net.wordrider.utilities.Swinger;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * @author Vity
 */
public final class CopyAction extends DefaultEditorKit.CopyAction {
    private final static CopyAction instance = new CopyAction();
    private static final String CODE = "CopyAction";

    public static CopyAction getInstance() {
        return instance;
    }

    private CopyAction() {
        putValue(Action.NAME, Lng.getLabel(CODE));
        putValue(Action.SHORT_DESCRIPTION, Lng.getHint(CODE));
        putValue(Action.MNEMONIC_KEY, new Integer(Lng.getMnemonic(CODE)));
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
        putValue(Action.SMALL_ICON, Swinger.getIcon("copy.gif"));
    }

    public boolean isEnabled() {
        final JTextComponent comp = getFocusedComponent();
        return comp != null && comp.isEnabled()
                && comp.getSelectedText() != null;
    }
}

