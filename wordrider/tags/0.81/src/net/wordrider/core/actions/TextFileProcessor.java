package net.wordrider.core.actions;

import net.wordrider.area.RiderArea;
import net.wordrider.core.AppPrefs;
import net.wordrider.core.Lng;
import net.wordrider.core.managers.AreaManager;
import net.wordrider.core.managers.FileInstance;
import net.wordrider.dialogs.ChooseFormatDialog;
import net.wordrider.files.ti68kformat.TITextFileInfo;
import net.wordrider.files.ti68kformat.TITextFileReader;
import net.wordrider.utilities.LogUtils;
import net.wordrider.utilities.Swinger;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * @author Vity
 */
class TextFileProcessor extends AbstractFileProcessor {
    public TextFileProcessor(File file) {
        super(file);
    }


    @Override
    public boolean process() {
        super.process();
        if (AppPrefs.getProperty(AppPrefs.SHOWINPUTFORMAT, true)) {
            final ChooseFormatDialog inputFormatDialog = new ChooseFormatDialog(getMainFrame(), ChooseFormatDialog.CHOOSE_INPUT_FORMAT);
            if (inputFormatDialog.getResult() != ChooseFormatDialog.RESULT_OK)
                return false;
        }

        final AreaManager areaManager = AreaManager.getInstance();
        FileInstance instance = (FileInstance) areaManager.isFileAlreadyOpened(getFile());
        final boolean reload;
        if (instance != null) {
            if (Swinger.getChoice(getMainFrame(), Lng.getLabel("message.confirm.FAE")) == Swinger.RESULT_YES)
                reload = true;
            else
                return false;
        } else
            reload = false;

        try {
            final TITextFileReader tiFile = new TITextFileReader();
            tiFile.openFromFile(getFile());
            if (!reload) {
                instance = new FileInstance(getFile(), tiFile.getTextFileInfo());
                instance.getFileInfo().setOutputFormat(AppPrefs.getProperty(AppPrefs.TIINPUTTEXTFORMAT, true) ? TITextFileInfo.OUTPUT_FORMAT_HIBVIEW : TITextFileInfo.OUTPUT_FORMAT_TXTRIDER);
                areaManager.openFileInstance(instance);
            }
            final RiderArea area = instance.getRiderArea();

            area.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            if (reload)
                areaManager.setActivateFileInstance(instance);
            return loadDocument(area, new BatchTextRead(instance, tiFile.getContent()));

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
