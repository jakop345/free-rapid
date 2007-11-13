package net.wordrider.core.swing;

import net.wordrider.area.actions.InsertPictureAction;
import net.wordrider.core.actions.OpenFileAction;
import net.wordrider.utilities.Swinger;
import net.wordrider.utilities.Utils;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class FileTransferHandlerImpl extends FileTransferHandler {
    private final static Logger logger = Logger.getLogger(FileTransferHandlerImpl.class.getName());

    protected void doDropAction(List files) {
        if (!EventQueue.isDispatchThread())
            logger.warning("This is not in EDT");
        for (Object file : files) {
            final File f = (File) file;
            if (f.exists() && f.isFile()) {
                final String ext = Utils.getExtension(f);
                if (Swinger.isImageExtension(ext) || Swinger.isStandardImageExtension(ext)) {
                    InsertPictureAction.getInstance().insertImage(f);
                } else OpenFileAction.open(f);
            }
        }
    }
}
