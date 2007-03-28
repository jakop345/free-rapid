package net.wordrider.core.actions;

import net.wordrider.core.AppPrefs;
import net.wordrider.core.Lng;
import net.wordrider.core.managers.AreaManager;
import net.wordrider.dialogs.RiderFileFilter;
import net.wordrider.utilities.Swinger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Vity
 */
public final class OpenFileAction extends CoreAction {
    private static final OpenFileAction instance = new OpenFileAction();

    public static OpenFileAction getInstance() {
        return instance;
    }

    private OpenFileAction() {
        super("OpenFileAction", KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK), "open.gif");
    }

    private static void updateFileFilters(final JFileChooser dialog, final List<RiderFileFilter> fileFilters, final String lastUsedKey) {
        for (RiderFileFilter fileFilter : fileFilters) dialog.addChoosableFileFilter(fileFilter);
        final int defaultFileFilter = AppPrefs.getProperty(lastUsedKey, fileFilters.size() - 1);
        if (defaultFileFilter >= 0 && defaultFileFilter < fileFilters.size())
            dialog.setFileFilter(fileFilters.get(defaultFileFilter));
    }

    private static File[] getOpenFileDialog(final List<RiderFileFilter> fileFilters, final String lastUsedFilterKey, final String lastFolderKey) {
        final File currentDirectory = new File(AppPrefs.getProperty(lastFolderKey, ""));
        //final JFileChooser fileDialog = (currentDirectory != null && currentDirectory.exists()) ? new JFileChooser(currentDirectory) : new JFileChooser();
        final JFileChooser fileDialog = new JFileChooser(currentDirectory);
        updateFileFilters(fileDialog, fileFilters, lastUsedFilterKey);
        fileDialog.setDialogTitle(Lng.getLabel("OpenFileAction.dialog.title"));
        fileDialog.setMultiSelectionEnabled(true);
        final int result = fileDialog.showOpenDialog(getMainFrame());
        if (result != JFileChooser.APPROVE_OPTION)
            return new File[0];
        else {
            AppPrefs.storeProperty(lastUsedFilterKey, fileFilters.indexOf(fileDialog.getFileFilter()));
            return fileDialog.getSelectedFiles();
        }
    }

    public static boolean open(final File openFile) {
        if (openFile == null)
            return false;
        if (!openFile.isFile() || !openFile.exists()) {
            Swinger.showErrorDialog(getMainFrame(), Lng.getLabel("message.error.FNF", openFile.getAbsolutePath()));
            return false;
        }
        return FileProcessorFactory.getInstance().getProcessorByFile(openFile).process();
    }


    public final void actionPerformed(final ActionEvent e) {
        final File[] openFiles = getOpenFileDialog(getFileFilters(), AppPrefs.LAST_USED_OPENFILTER, AppPrefs.LASTOPENFOLDER_KEY);
        final int fileCount = openFiles.length;
        for (int i = 0; i < fileCount; i++) {
            if (!open(openFiles[i]))
                break;
        }
        AreaManager.getInstance().grabActiveFocus();
    }

    private static List<RiderFileFilter> getFileFilters() {
        final List<RiderFileFilter> result = new ArrayList<RiderFileFilter>(6);
        result.add(new RiderFileFilter(new String[]{"txt"}, "OpenFileAction.dialog.filterTxt"));
        result.add(new RiderFileFilter(new String[]{"9xy"}, "OpenFileAction.dialog.filter9xy"));
        result.add(new RiderFileFilter(new String[]{"89y"}, "OpenFileAction.dialog.filter89y"));
        result.add(new RiderFileFilter(new String[]{"92t"}, "OpenFileAction.dialog.filter92"));
        result.add(new RiderFileFilter(new String[]{"9xt"}, "OpenFileAction.dialog.filter9x"));
        result.add(new RiderFileFilter(new String[]{"89t"}, "OpenFileAction.dialog.filter89"));
        result.add(new RiderFileFilter(new String[]{"89t", "9xt", "92t", "89y","9xy", "txt"}, "OpenFileAction.dialog.allsupported"));
        return result;
    }


}