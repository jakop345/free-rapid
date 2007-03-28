package net.wordrider.core.actions;

import net.wordrider.core.AppPrefs;
import net.wordrider.utilities.Consts;
import net.wordrider.utilities.Utils;

import java.awt.event.ActionEvent;
import java.io.File;

/**
 * @author Vity
 */
public class OpenKeymapAction extends OpenPDFDocumentAction {
    private final static OpenKeymapAction instance = new OpenKeymapAction();

    private OpenKeymapAction() {
        super("OpenKeymapAction", null, null);
    }


    public static OpenKeymapAction getInstance() {
        return instance;
    }

    public void actionPerformed(ActionEvent e) {
        final String s = Utils.addFileSeparator(AppPrefs.getAppPath() + Consts.PDF_DIRECTORY) + Consts.QUICK_REF_PDF;
        super.openDocument(new File(s));
    }
}
