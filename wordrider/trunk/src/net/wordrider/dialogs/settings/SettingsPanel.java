package net.wordrider.dialogs.settings;

import net.wordrider.utilities.Swinger;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 * @author Vity
 */
abstract class SettingsPanel extends JPanel {
    final OptionsGroupManager manager;

    public SettingsPanel(final SettingsDialog dialog, final String label) {
        super();
        manager = new OptionsGroupManager(dialog);
        this.setBorder(BorderFactory.createCompoundBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), label), BorderFactory.createEmptyBorder(6, 0, 0, 2)));
        init();
    }

    public final void focusFirstComponent() {
        Swinger.inputFocus((JComponent) this.getFocusCycleRootAncestor().getFocusTraversalPolicy().getFirstComponent(this));
    }

    public final IOptionsManager getOptionsManager() {
        return manager;
    }

    protected abstract void init();
}
