package net.wordrider.core.actions;

import net.wordrider.core.AppPrefs;
import net.wordrider.core.managers.AreaManager;
import net.wordrider.utilities.Consts;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * @author Vity
 */
public final class OpenSampleFileAction extends CoreAction {

    public OpenSampleFileAction() {
        super("OpenSampleFileAction", null, null);
    }

    public final void actionPerformed(final ActionEvent e) {
        final File sampleFile = new File(AppPrefs.getAppPath() + Consts.SAMPLE_FILE);
        if (sampleFile.exists() && sampleFile.isFile()) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    final AreaManager areaManager = AreaManager.getInstance();
                    if (OpenFileAction.open(sampleFile))
                        areaManager.grabActiveFocus();
                }
            });
        }
    }


}
