package net.wordrider.core.actions;

import net.wordrider.area.RiderArea;
import net.wordrider.core.AppPrefs;
import net.wordrider.core.MainApp;

import javax.swing.text.Document;
import java.awt.*;
import java.io.File;
import java.util.logging.Logger;

/**
 * @author Vity
 */
abstract class AbstractFileProcessor implements FileProcessor {
    final static Logger logger = Logger.getLogger(AbstractFileProcessor.class.getName());
    private final File file;

    public AbstractFileProcessor(File file) {
        this.file = file;
    }


    public boolean process() {
        AppPrefs.storeProperty(AppPrefs.LASTOPENFOLDER_KEY, getFile().getAbsolutePath());
        return false;
    }

    File getFile() {
        return file;
    }

    boolean loadDocument(RiderArea area, DocumentProducer producer) {
        final RiderSwingWorker worker = new LoadFileAction(producer);
        worker.init();

        final Document result = (Document) worker.get();
        //final Document result = testDocument(area, file.getContent());
        if (result != null) {
            //area.repaint();
            area.setDocument(result);

            // temp.setDocument(new RiderDocument());
            area.setCursor(Cursor.getDefaultCursor());
            return true;
        } else return false;
    }

    Frame getMainFrame() {
        return MainApp.getInstance().getMainAppFrame();
    }        

}
