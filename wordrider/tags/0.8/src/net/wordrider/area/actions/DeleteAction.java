package net.wordrider.area.actions;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.event.ActionEvent;

/**
 * User: Vity
 */
public final class DeleteAction extends AbstractAction {
    private final JTextComponent comp;

    public DeleteAction(JTextComponent comp) {
        super("Delete");
        this.comp = comp;
    }

    public final void actionPerformed(ActionEvent e) {
        comp.replaceSelection(null);
    }

    public final boolean isEnabled() {
        return comp.isEditable()
                && comp.isEnabled()
                && comp.getSelectedText() != null;
    }
}