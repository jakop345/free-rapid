package net.wordrider.core.actions;

import net.wordrider.utilities.BrowserControl;

import javax.swing.*;
import java.io.File;
import java.util.logging.Logger;

/**
 * @author Vity
 */
abstract class OpenPDFDocumentAction extends CoreAction{
    private final static Logger logger = Logger.getLogger(OpenPDFDocumentAction.class.getName());
    protected OpenPDFDocumentAction(final String actionCode, final KeyStroke keyStroke, final String smallIcon) {
        super(actionCode, keyStroke, smallIcon);
    }

    void openDocument(final File document) {
        if (document.exists() && document.isFile()) {
            BrowserControl.openPDF(document);
            logger.info("Opening PDF file " + document.getPath());
        }
    }
}
