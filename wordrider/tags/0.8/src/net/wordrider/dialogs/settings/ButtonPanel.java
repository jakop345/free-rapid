package net.wordrider.dialogs.settings;

import net.wordrider.dialogs.layouts.EqualsLayout;
import net.wordrider.utilities.Swinger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Vity
 */
final class ButtonPanel extends JPanel {
    private JButton btnSave;
    private JButton btnApply;
    private JButton btnCancel;
    private final SettingsDialog dialog;

    public ButtonPanel(final SettingsDialog dialog) {
        super();    //call to super
        this.dialog = dialog;
        init();
    }

    private void init() {
        this.setLayout(new EqualsLayout(5));
        this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(4, 4, 4, 8), this.getBorder()));
        final Dimension dimension = new Dimension(85, 25);
        btnSave = Swinger.getButton("settings.btn.save", new SaveAction());
        btnApply = Swinger.getButton("settings.btn.apply", new ApplyAction());
        btnApply.getAction().setEnabled(false);
        btnCancel = Swinger.getButton("settings.btn.cancel", new CancelAction());

        btnSave.setMinimumSize(dimension);
        btnCancel.setMinimumSize(dimension);
        btnApply.setMinimumSize(dimension);
        this.add(btnSave);
        this.add(btnApply);
        this.add(btnCancel);
    }

    private void applyChanges() {
        dialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        dialog.getActiveSettingsPanel().getOptionsManager().applyChanges();
        dialog.setCursor(Cursor.getDefaultCursor());
    }

    private final class ApplyAction extends AbstractAction {
        public ApplyAction() {
            super("");
        }

        public final void actionPerformed(final ActionEvent e) {
            applyChanges();
        }
    }

    private final class SaveAction extends AbstractAction {
        public SaveAction() {
            super("");
        }

        public final void actionPerformed(final ActionEvent e) {
            applyChanges();
            dialog.doClose();
        }
    }

    private final class CancelAction extends AbstractAction {
        public CancelAction() {
            super("");
        }

        public final void actionPerformed(final ActionEvent e) {
            //   dialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            //  dialog.getActiveSettingsPanel().getOptionsManager().restoreChanged();
            dialog.doClose();
        }
    }

    public final AbstractButton getApplyButton() {
        return btnApply;
    }

    public final AbstractButton getCancelButton() {
        return btnCancel;
    }

    public final AbstractButton getOkButton() {
        return btnSave;
    }
}
