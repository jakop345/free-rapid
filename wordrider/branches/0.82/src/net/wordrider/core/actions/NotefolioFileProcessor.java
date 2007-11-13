package net.wordrider.core.actions;

import net.wordrider.area.RiderArea;
import net.wordrider.core.AppPrefs;
import net.wordrider.core.Lng;
import net.wordrider.core.managers.AreaManager;
import net.wordrider.core.managers.FileInstance;
import net.wordrider.files.ti68kformat.TINoteFolioReader;
import net.wordrider.files.ti68kformat.TITextFileInfo;
import net.wordrider.utilities.LogUtils;
import net.wordrider.utilities.Swinger;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * @author Vity
 */
 class NotefolioFileProcessor extends AbstractFileProcessor {

    public NotefolioFileProcessor(File file) {
        super(file);
    }

    @Override
    public boolean process() {
        super.process();

        final AreaManager areaManager = AreaManager.getInstance();

        try {
            final TINoteFolioReader tiFile = new TINoteFolioReader();
            final File inputFile = getFile();
            tiFile.openFromFile(inputFile);

            FileInstance instance = new FileInstance(null, tiFile.getTextFileInfo());
            instance.getFileInfo().setOutputFormat(AppPrefs.getProperty(AppPrefs.TIINPUTTEXTFORMAT, true) ? TITextFileInfo.OUTPUT_FORMAT_HIBVIEW : TITextFileInfo.OUTPUT_FORMAT_TXTRIDER);
            areaManager.openFileInstance(instance);

            final RiderArea area = instance.getRiderArea();
            area.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            instance.setModified(true);
            final boolean result = loadDocument(area, new BatchNotefolioRead(instance, tiFile.getContent()));
            instance.setModified(true);
            return result;

        } catch (IOException e) {
            LogUtils.processException(logger, e);
            Swinger.showErrorDialog(getMainFrame(), Lng.getLabel("message.error.reading", e.getMessage()));
            return false;
        } catch (Exception e) {
            logger.warning(e.getMessage());
            Swinger.showErrorDialog(getMainFrame(), e.getMessage());
            return false;
        }
    }

}
