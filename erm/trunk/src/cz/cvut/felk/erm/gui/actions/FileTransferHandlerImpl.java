package cz.cvut.felk.erm.gui.actions;

import cz.cvut.felk.erm.utilities.Utils;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
public class FileTransferHandlerImpl extends FileTransferHandler {
    private final static Logger logger = Logger.getLogger(FileTransferHandlerImpl.class.getName());

    public FileTransferHandlerImpl() {
        super();
    }

    protected void doDropAction(List files) {
        if (!EventQueue.isDispatchThread())
            logger.warning("This is not on EDT");
        logger.info("Doing drop action");
        for (Object file : files) {
            final File f = (File) file;
            if (f.exists() && f.isFile()) {
                logger.info("Trying to open file " + f.getAbsolutePath());
                final String ext = Utils.getExtension(f);
                //e OpenFileAction.open(f);
            }
        }
    }
}
