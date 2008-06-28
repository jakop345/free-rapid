package cz.cvut.felk.erm.gui.dialogs.filechooser;

import cz.cvut.felk.erm.core.AppPrefs;
import cz.cvut.felk.erm.core.UserProp;
import cz.cvut.felk.erm.swing.Swinger;
import cz.cvut.felk.erm.utilities.Utils;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ladislav Vitasek
 */
public class OpenSaveDialogFactory {

    private OpenSaveDialogFactory() {
    }

//    public static File[] getOpenSoundDialog(String currentPath) {
//        final List<EnhancedFileFilter> filters = new ArrayList<EnhancedFileFilter>(4);
//        filters.add(EnhancedFileFilter.createFilter(new String[]{"wav"}, "filterWav"));
//        filters.add(EnhancedFileFilter.createFilter(new String[]{"mid"}, "filterMid"));
//        filters.add(EnhancedFileFilter.createFilter(new String[]{"au"}, "filterAu"));
//        filters.add(EnhancedFileFilter.createFilter(new String[]{"wav", "mid", "au"}, "soundDialog.allSupported"));
//
//        return getOpenFileDialog(filters, FWProp.LAST_USED_SOUND_FILTER, currentPath);
//    }

    public static File getSaveLogDialog() {
        final List<EnhancedFileFilter> filters = new ArrayList<EnhancedFileFilter>(3);
        filters.add(EnhancedFileFilter.createFilter(new String[]{"txt"}, "filterTxt"));
        filters.add(EnhancedFileFilter.createFilter(new String[]{"log"}, "filterLog"));
        filters.add(EnhancedFileFilter.createAllFilesFilter());
        final String path = AppPrefs.getProperty(UserProp.LAST_USED_FOLDER_SAVELOG, "report.log");

        final String defaultName = AppPrefs.getProperty(UserProp.LAST_SAVELOG_FILENAME, new File(path).getName());
        final File result = getSaveFileDialog(filters, UserProp.LAST_SAVELOG_FILTER, UserProp.LAST_USED_FOLDER_SAVELOG, defaultName);
        if (result != null) {
            AppPrefs.storeProperty(UserProp.LAST_SAVELOG_FILENAME, result.getName());
        }
        return result;
    }

    public static File[] getChooseJARorZIPFileDialog() {
        final List<EnhancedFileFilter> filters = new ArrayList<EnhancedFileFilter>(3);
        filters.add(EnhancedFileFilter.createFilter(new String[]{"zip"}, "filterZIP"));
        filters.add(EnhancedFileFilter.createFilter(new String[]{"jar"}, "filterJAR"));
        filters.add(EnhancedFileFilter.createFilter(new String[]{"jar", "zip"}, "filterJavaLibraries"));
        return getOpenFileDialog(filters, UserProp.OPEN_JAR_LAST_USED_FILTER, UserProp.OPEN_JAR_LAST_USED_FOLDER);
    }


    @SuppressWarnings({"SuspiciousMethodCalls"})
    private static File[] getOpenFileDialog(final List<EnhancedFileFilter> fileFilters, final String lastUsedFilterKey, final String folderPathKey) {

        final OpenFileChooser fileDialog = new OpenFileChooser(new File(AppPrefs.getProperty(folderPathKey, "")));
        fileDialog.updateFileFilters(fileFilters, lastUsedFilterKey);
        fileDialog.setAcceptAllFileFilterUsed(false);
        fileDialog.setMultiSelectionEnabled(false);
        final int result = fileDialog.showOpenDialog(Frame.getFrames()[0]);
        if (result != JFileChooser.APPROVE_OPTION)
            return new File[0];
        else {
            AppPrefs.storeProperty(lastUsedFilterKey, fileFilters.indexOf(fileDialog.getFileFilter()));
            AppPrefs.storeProperty(folderPathKey, fileDialog.getSelectedFile().getPath());
            return new File[]{fileDialog.getSelectedFile()};
        }
    }

    @SuppressWarnings({"SuspiciousMethodCalls"})
    private static File getSaveFileDialog(final List<EnhancedFileFilter> fileFilters, final String lastUsedFilterKey, final String folderPathKey, final String fileName) {
        final SaveFileChooser fileDialog = new SaveFileChooser(new File(AppPrefs.getProperty(folderPathKey, "")));
        fileDialog.setMultiSelectionEnabled(false);
        fileDialog.setAcceptAllFileFilterUsed(false);
        fileDialog.setFileName(fileName);
        fileDialog.updateFileFilters(fileFilters, lastUsedFilterKey);
        final int result = fileDialog.showSaveDialog(Frame.getFrames()[0]);
        if (result != JFileChooser.APPROVE_OPTION)
            return null;
        else {
            File f = fileDialog.getSelectedFile();
            if (f == null)
                return null;
            final FileFilter usedFilter = fileDialog.getFileFilter();
            if (usedFilter instanceof IFileType && (Utils.getExtension(f)) == null) {
                final String extension = ((IFileType) usedFilter).getExtension();
                f = new File(f.getPath().concat(".").concat(extension));
            }
            if (f.isFile() && f.exists()) {
                final ResourceMap map = Swinger.getResourceMap(JAppFileChooser.class);
                final int choice = Swinger.getChoice(map.getString("message.confirm.overwrite"));
                if (choice == Swinger.RESULT_NO)
                    return null;
            }
            AppPrefs.storeProperty(lastUsedFilterKey, fileFilters.indexOf(usedFilter));
            AppPrefs.storeProperty(folderPathKey, f.getPath());
            return f;
        }
    }
}
