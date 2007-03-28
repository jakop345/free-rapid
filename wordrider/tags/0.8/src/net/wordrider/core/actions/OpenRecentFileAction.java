package net.wordrider.core.actions;

import net.wordrider.core.managers.AreaManager;
import net.wordrider.utilities.Utils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * @author Vity
 */
public final class OpenRecentFileAction extends CoreAction {
    private final File recentFile;

    public OpenRecentFileAction(final File file, final char mnemonic) {
        super("OpenRecentFileAction", mnemonic + " " + Utils.shortenFileName(file.toString(), 50), (int) mnemonic, null);
        this.recentFile = file;
        putValue(Action.SHORT_DESCRIPTION, file.toString());
    }

    public final void actionPerformed(final ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final AreaManager areaManager = AreaManager.getInstance();
                if (!OpenFileAction.open(recentFile)) {
                    areaManager.getRecentFilesManager().removeBadFile(recentFile);
                } else areaManager.grabActiveFocus();
            }
        });
    }

}
