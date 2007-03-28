package net.wordrider.core.actions;

import net.wordrider.core.managers.AreaManager;
import net.wordrider.core.managers.FileInstance;
import net.wordrider.core.AppPrefs;
import net.wordrider.core.Lng;
import net.wordrider.files.ti68kformat.TINoteFolioReader;
import net.wordrider.files.ti68kformat.TITextFileInfo;
import net.wordrider.files.ImportableFileReader;
import net.wordrider.files.miscformat.PureTextReader;
import net.wordrider.area.RiderArea;
import net.wordrider.utilities.LogUtils;
import net.wordrider.utilities.Swinger;

import java.io.File;
import java.io.IOException;
import java.awt.*;

/**
 * @author Vity
 */
 class PureTextFileProcessor extends AbstractFileProcessor {

    public PureTextFileProcessor(File file) {
        super(file);
    }

    @Override
    public boolean process() {
        super.process();

        final AreaManager areaManager = AreaManager.getInstance();

        try {
            final ImportableFileReader importableFile = new PureTextReader();
            final File inputFile = getFile();
            importableFile.openFromFile(inputFile);

            FileInstance instance = new FileInstance(null, new TITextFileInfo());
            instance.getFileInfo().setOutputFormat(AppPrefs.getProperty(AppPrefs.TIINPUTTEXTFORMAT, true) ? TITextFileInfo.OUTPUT_FORMAT_HIBVIEW : TITextFileInfo.OUTPUT_FORMAT_TXTRIDER);
            areaManager.openFileInstance(instance);

            final RiderArea area = instance.getRiderArea();
            area.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            instance.setModified(true);
            final boolean result = loadDocument(area, new BatchPureTextReader(instance, importableFile.getContent()));
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
