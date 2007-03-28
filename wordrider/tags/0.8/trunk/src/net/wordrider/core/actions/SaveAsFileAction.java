package net.wordrider.core.actions;

import net.wordrider.core.AppPrefs;
import net.wordrider.core.Lng;
import net.wordrider.core.managers.AreaManager;
import net.wordrider.core.managers.interfaces.IFileInstance;
import net.wordrider.dialogs.RiderFileFilter;
import net.wordrider.dialogs.SaveSettingsDialog;
import net.wordrider.files.ti68kformat.*;
import net.wordrider.utilities.LogUtils;
import net.wordrider.utilities.Swinger;
import net.wordrider.utilities.Utils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.FileChooserUI;
import javax.swing.plaf.basic.BasicFileChooserUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public final class SaveAsFileAction extends CoreAction {
    private static final SaveAsFileAction instance = new SaveAsFileAction();
    //  private static File currentDirectory;
    private static final String CODE = "SaveAsFileAction";
    private static final String LASTFOLDER_KEY = AppPrefs.LASTOPENFOLDER_KEY;
    private final static Logger logger = Logger.getLogger(SaveAsFileAction.class.getName());

    public static SaveAsFileAction getInstance() {
        return instance;
    }

    private SaveAsFileAction() {
        super(CODE, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK), "save_as.gif");
    }

    static boolean runSaveProcess(final JFrame frame, final File f, final IFileInstance instance, final boolean showInfoWindow) {
        try {
            final net.wordrider.core.actions.TextRiderProcess process = new net.wordrider.core.actions.TextRiderProcess();
            final String s = process.getTxtRiderTextForDocument((StyledDocument) instance.getRiderArea().getDocument(), instance.getFileInfo(), true);
            logger.info(s);
            final TITextFileWriter file = new TITextFileWriter();
            file.setFileInfo(instance.getFileInfo());
            file.setTextContent(s);
            file.saveToFile(f);
            instance.setFile(f);
        } catch (BadLocationException ex) {
            Swinger.showErrorDialog(frame, Lng.getLabel("message.error.Fwriting", ex.getMessage()));
            LogUtils.processException(logger, ex);
            return false;
        } catch (IOException ex) {
            logger.severe(ex.getMessage());
            Swinger.showErrorDialog(frame, Lng.getLabel("message.error.saving", ex.getMessage()));
            return false;
        }
        // == null - no info window
        if (frame != null && showInfoWindow && AppPrefs.getProperty(AppPrefs.INFO_SUCCESFUL, true))
            Swinger.showInformationDialog(frame, Lng.getLabel("message.information.savedSuccess", f.getAbsolutePath()));
        return true;
    }

    private static boolean runImageSaveProcess(final JFrame frame, final File f, final Image image, final TIFileInfo info) {
        try {
            final TIImageFileWriter file = new TIImageFileWriter(image);
            file.setFileInfo(info);
            file.saveToFile(f);
        } catch (IOException ex) {
            logger.severe(ex.getMessage());
            Swinger.showErrorDialog(frame, Lng.getLabel("message.error.saving", ex.getMessage()));
            return false;
        }
        return true;
    }

    static TIFileInfo getSaveSettings(final Frame frame, final TIFileInfo info, final int statusWindow) {
        final SaveSettingsDialog saveSettingsDialog = new SaveSettingsDialog(frame, info, statusWindow);
        return saveSettingsDialog.getResult();
    }

    private static File uniSaveProcess(final Frame frame, final List<FileFilter> fileFilters, final FileFilter selectedFileFilter, final String fileName, final String filterAppPrefs) {
        final File currentDirectory = new File(AppPrefs.getProperty(LASTFOLDER_KEY, ""));
        final JFileChooser fileDialog = new JFileChooser(currentDirectory);
        fileDialog.setDialogTitle(Lng.getLabel(CODE + ".dialog.title"));
        final FileChooserUI chooserUI = fileDialog.getUI();
        if (chooserUI instanceof BasicFileChooserUI)
            ((BasicFileChooserUI) chooserUI).setFileName(fileName);
        for (FileFilter fileFilter : fileFilters) fileDialog.addChoosableFileFilter(fileFilter);
        fileDialog.setFileFilter(selectedFileFilter);
        if (fileDialog.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION)
            return null;
        final FileFilter usedFilter = fileDialog.getFileFilter();
        if (filterAppPrefs != null) {
            AppPrefs.storeProperty(filterAppPrefs, fileFilters.indexOf(fileDialog.getFileFilter()));
        }
        File f = fileDialog.getSelectedFile();
        if (f == null)
            return null;
        //final String activeExt;
        //fill extension for lazy user
        if (usedFilter instanceof IFileType && (Utils.getExtension(f)) == null) {
            final String extension = ((IFileType) usedFilter).getExtension();
            //if (!activeExt.equalsIgnoreCase(extension))
            f = new File(f.getPath().concat(".").concat(extension));
        }
        if (f.exists()) {
            final int result = Swinger.getChoice(frame, Lng.getLabel("message.confirm.overwrite"));
            if (result == Swinger.RESULT_NO)
                return null;    //?
        }
        AppPrefs.storeProperty(LASTFOLDER_KEY, f.getAbsolutePath());
        return f;
    }


    static boolean saveAsProcess(final boolean showInfoWindow) {
        final JFrame frame = getMainFrame();
        final IFileInstance instance = AreaManager.getInstance().getActiveInstance();
        if (instance == null)
            return false;
        final TITextFileInfo info = (TITextFileInfo) getSaveSettings(frame, instance.getFileInfo(), SaveSettingsDialog.STATUS_TEXTFILE);
        if (info == null)
            return false;
        else
            instance.setFileInfo(info);
        final List<FileFilter> fileFilters = getFileFilters();
        final int filterCount = fileFilters.size();
        int defaultFileFilter = AppPrefs.getProperty(AppPrefs.LAST_USED_SAVEFILTER, filterCount - 1);
        if (defaultFileFilter < 0 || defaultFileFilter >= filterCount - 1)
            defaultFileFilter = filterCount - 1;
        final File processFile = uniSaveProcess(frame, fileFilters, fileFilters.get(defaultFileFilter), info.getVarName(), AppPrefs.LAST_USED_SAVEFILTER);
        return processFile != null && runSaveProcess(frame, processFile, instance, showInfoWindow);
    }

    public static File saveAsImageProcess(final TIImageFileInfo inputInfo, final Image image) {
        final JFrame frame = getMainFrame();
        final TIImageFileInfo info = (TIImageFileInfo) getSaveSettings(frame, inputInfo, SaveSettingsDialog.STATUS_IMAGE);
        if (info == null)
            return null;
        inputInfo.setInsertIntoDocument(info.isInsertIntoDocument());
        final boolean formatTI92 = AppPrefs.getProperty(AppPrefs.TI92IMAGEFORMAT, false);
        final List<FileFilter> fileFilters = new ArrayList<FileFilter>(2);
        final FileFilter filter;
        if (formatTI92) {
            fileFilters.add(new RiderFileFilter(new String[]{"92i"}, "picturedialog.ti92files"));
            fileFilters.add(filter = new RiderFileFilter(new String[]{"9xi"}, "picturedialog.ti9xfiles"));
        } else fileFilters.add(filter = new RiderFileFilter(new String[]{"89i"}, "picturedialog.ti89files"));
        final File processFile = uniSaveProcess(frame, fileFilters, filter, info.getVarName(), null);
        if (processFile != null && runImageSaveProcess(frame, processFile, image, info))
            return processFile;
        return null;
    }

    private static List<FileFilter> getFileFilters() {
        final List<FileFilter> result = new ArrayList<FileFilter>(2);
        result.add(new RiderFileFilter(new String[]{"92t"}, "OpenFileAction.dialog.filter92"));
        result.add(new RiderFileFilter(new String[]{"9xt"}, "OpenFileAction.dialog.filter9x"));
        result.add(new RiderFileFilter(new String[]{"89t"}, "OpenFileAction.dialog.filter89"));
        return result;
    }

    public final void actionPerformed(final ActionEvent e) {
        saveAsProcess(true);
    }
}
