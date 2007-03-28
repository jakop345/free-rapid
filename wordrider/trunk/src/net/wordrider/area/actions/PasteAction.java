package net.wordrider.area.actions;

import net.wordrider.area.RiderArea;
import net.wordrider.core.Lng;
import net.wordrider.utilities.Swinger;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * @author Vity
 */

/**
 */
public final class PasteAction extends DefaultEditorKit.PasteAction {
    private final static PasteAction instance = new PasteAction();
    private static final String code = "PasteAction";

    public static PasteAction getInstance() {
        return instance;
    }

    private PasteAction() {
        super();
        putValue(Action.NAME, Lng.getLabel(code));
        putValue(Action.SHORT_DESCRIPTION, Lng.getHint(code));
        putValue(Action.MNEMONIC_KEY, new Integer(Lng.getMnemonic(code)));
        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
        putValue(Action.SMALL_ICON, Swinger.getIcon("paste.gif"));
    }

    public boolean isEnabled() {
        final JTextComponent comp = getFocusedComponent();
        if (comp != null) {
            if (comp.isEditable() && comp.isEnabled()) {
                final Transferable contents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this);
                return contents.isDataFlavorSupported(DataFlavor.stringFlavor) || (contents.isDataFlavorSupported(RiderArea.df) && comp instanceof RiderArea);
            } else
                return false;
        }
        return false;
    }

}
