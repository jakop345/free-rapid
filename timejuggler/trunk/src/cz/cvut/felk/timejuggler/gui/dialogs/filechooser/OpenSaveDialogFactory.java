package cz.cvut.felk.timejuggler.gui.dialogs.filechooser;

import cz.cvut.felk.timejuggler.core.AppPrefs;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Vity
 */
public class OpenSaveDialogFactory {
    private OpenSaveDialogFactory() {
    }

    public static File[] getOpenSoundDialog(String currentPath) {
        final List<EnhancedFileFilter> filters = new ArrayList<EnhancedFileFilter>(4);
        filters.add(new EnhancedFileFilter(new String[]{"wav"}, "soundDialog.filterWav"));
        filters.add(new EnhancedFileFilter(new String[]{"mid"}, "soundDialog.filterMid"));
        filters.add(new EnhancedFileFilter(new String[]{"au"}, "soundDialog.filterAu"));
        filters.add(new EnhancedFileFilter(new String[]{"wav", "mid", "au"}, "soundDialog.allSupported"));

        return getOpenFileDialog(filters, AppPrefs.LAST_USED_SOUND_FILTER, currentPath);
    }


    @SuppressWarnings({"SuspiciousMethodCalls"})
    private static File[] getOpenFileDialog(final List<EnhancedFileFilter> fileFilters, final String lastUsedFilterKey, final String folderPath) {

        final JFileChooser fileDialog = new JAppFileChooser(new File(folderPath));
        updateFileFilters(fileDialog, fileFilters, lastUsedFilterKey);
        fileDialog.setAcceptAllFileFilterUsed(false);
        fileDialog.setMultiSelectionEnabled(false);
        final int result = fileDialog.showOpenDialog(Frame.getFrames()[0]);
        if (result != JFileChooser.APPROVE_OPTION)
            return new File[0];
        else {
            AppPrefs.storeProperty(lastUsedFilterKey, fileFilters.indexOf(fileDialog.getFileFilter()));
            return new File[]{fileDialog.getSelectedFile()};
        }
    }

    private static void updateFileFilters(final JFileChooser dialog, final List<EnhancedFileFilter> fileFilters, final String lastUsedKey) {
        for (EnhancedFileFilter fileFilter : fileFilters) dialog.addChoosableFileFilter(fileFilter);
        final int defaultFileFilter = AppPrefs.getProperty(lastUsedKey, fileFilters.size() - 1);
        if (defaultFileFilter >= 0 && defaultFileFilter < fileFilters.size())
            dialog.setFileFilter(fileFilters.get(defaultFileFilter));
    }

}
