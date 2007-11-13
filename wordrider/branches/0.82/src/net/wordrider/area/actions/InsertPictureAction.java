package net.wordrider.area.actions;

import net.wordrider.area.RiderArea;
import net.wordrider.area.RiderDocument;
import net.wordrider.area.RiderStyles;
import net.wordrider.core.AppPrefs;
import net.wordrider.core.Lng;
import net.wordrider.core.MainApp;
import net.wordrider.core.MainAppFrame;
import net.wordrider.core.managers.AreaManager;
import net.wordrider.core.managers.FileInstance;
import net.wordrider.dialogs.ChooseFormatDialog;
import net.wordrider.dialogs.PictureDialog;
import net.wordrider.dialogs.RiderFileFilter;
import net.wordrider.dialogs.pictures.FilterDialog;
import net.wordrider.files.InvalidDataTypeException;
import net.wordrider.files.NotSupportedFileException;
import net.wordrider.files.ti68kformat.TIImageDecoder;
import net.wordrider.utilities.LogUtils;
import net.wordrider.utilities.Swinger;
import net.wordrider.utilities.Utils;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.Utilities;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public final class InsertPictureAction extends TextAreaAction {
    private static final InsertPictureAction instance = new InsertPictureAction();
    private static final String key = "lastPictureFolder";
    private final static Logger logger = Logger.getLogger(InsertPictureAction.class.getName());

    public static InsertPictureAction getInstance() {
        return instance;
    }

    private InsertPictureAction() {
        super("InsertPictureAction", KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK), "ins_pic_v2.gif");
    }

    private static boolean checkFileName(final File f, final TIImageDecoder image) {
        String pureFileName = f.getName();
        final int dotIndex = pureFileName.lastIndexOf('.');
        if (dotIndex != -1)
            pureFileName = pureFileName.substring(0, dotIndex);
        return pureFileName.equals(image.getFileInfo().getVarName());
    }

    private static void insertTI89ImageFile(final Frame frame, final RiderArea area, final File f) {
        int caretPosition = area.getCaretPosition();
        if (area.getSelectionStart() != area.getSelectionEnd())
            area.select(caretPosition, caretPosition);
        try {
            final boolean rowstart = Utilities.getRowStart(area, caretPosition) == caretPosition;
            if (!rowstart) {
                area.getDocument().insertString(area.getCaretPosition(), "\n", null);
                ++caretPosition;
            }
            final TIImageDecoder ti = new TIImageDecoder();
            try {
                if (!ti.openFromFile(f)) {
                    Swinger.showWarningDialog(frame, Lng.getLabel("picture.crcerror"));
                }
            } catch (InvalidDataTypeException ex) {
                logger.warning(ex.getMessage());
                Swinger.showErrorDialog(frame, ex.getMessage());
                return;
            } catch (NotSupportedFileException ex) {
                logger.warning(ex.getMessage());
                Swinger.showErrorDialog(frame, ex.getMessage());
                return;
            } catch (IOException ex) {
                LogUtils.processException(logger, ex);
                Swinger.showErrorDialog(frame, ex.getMessage());
                return;
            }
            if (!checkFileName(f, ti)) {
                final int result = Swinger.getChoice(frame, Lng.getLabel("picture.filenameerror"));
                if (result != Swinger.RESULT_YES)
                    return;
            }
            final RiderDocument doc = area.getDoc();
            area.makeGroupChange(true);
            final Style style = doc.getLogicalStyle(caretPosition);
            doc.setLogicalStyle(caretPosition, RiderStyles.alignmentCenteredStyle);

            doc.removeBookmark(area.getDoc().getParagraphElement(caretPosition));
            doc.insertPicture(caretPosition, Toolkit.getDefaultToolkit().createImage(ti));
            doc.insertString(caretPosition + 1, "\n", null);
            doc.setLogicalStyle(caretPosition + 2, style);
            area.makeGroupChange(false);
        } catch (BadLocationException ex) {
            LogUtils.processException(logger, ex);
        }
    }

    private static Image loadInputImage(final MediaTracker mt, final Image origImage) {
        mt.addImage(origImage, 0);
        try {
            mt.waitForAll();
        } catch (Exception e) {
            LogUtils.processException(logger, e);
        }
        return origImage;
    }

    public static void insertImageFromFilter(final Frame frame, final RiderArea area, final File inputFile, final Image inputImage) {
        final boolean resultDialog;
        if (AppPrefs.getProperty(AppPrefs.SHOW_IMAGEFORMAT, true)) {
            final ChooseFormatDialog imageFormatDialog = new ChooseFormatDialog(frame, ChooseFormatDialog.CHOOSE_OUTPUT_FORMAT);
            resultDialog = imageFormatDialog.getResult() == ChooseFormatDialog.RESULT_OK;
        } else resultDialog = true;
        if (resultDialog) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    final FilterDialog filterDialog = new FilterDialog(frame, inputFile, inputImage);
                    filterDialog.setVisible(true);
                    final File outputFile = filterDialog.getOutputFile();
                    if (outputFile != null && filterDialog.getFileInfo().isInsertIntoDocument())
                        insertTI89ImageFile(frame, area, outputFile);
                }
            });
        }
    }

    public final void insertImage(File f) {
        final AreaManager areaManager = getAreaManager();
        if (areaManager.hasOpenedInstance()) {
            final FileInstance activeInstance = areaManager.getActiveInstance();
            final MainAppFrame frame = MainApp.getInstance().getMainAppFrame();
            final RiderArea riderArea = activeInstance.getRiderArea();
            insertImage(riderArea, frame, f);
        }
    }


    public final void actionPerformed(final ActionEvent e) {
        final RiderArea area = getRiderArea(e);
        if (area == null)
            return;
        final File currentDirectory = new File(AppPrefs.getProperty(key, ""));
        final PictureDialog fileDialog = currentDirectory.exists() ? new PictureDialog(currentDirectory) : new PictureDialog();
        final MainAppFrame frame = MainApp.getInstance().getMainAppFrame();
        fileDialog.freeResources();
        if (fileDialog.showInsertDialog(frame) != PictureDialog.APPROVE_OPTION)
            return;
        File f = fileDialog.getSelectedFile();
        if (f == null)
            return;

        if (!f.exists()) {
            String extension = Utils.getExtension(f);
            if (extension == null) {
                f = new File(f.getAbsolutePath() + "." + ((RiderFileFilter) fileDialog.getFileFilter()).getExtension());
                if (!f.exists()) {
                    Swinger.showErrorDialog(frame, Lng.getLabel("message.error.FNF", fileDialog.getSelectedFile()));
                    return;
                }
            }
        }
        AppPrefs.storeProperty(key, f.getAbsolutePath());
        insertImage(area, frame, f);
    }

    private void insertImage(RiderArea area, MainAppFrame frame, File f) {
        final String extension = Utils.getExtension(f);
        if (extension != null && Swinger.isImageExtension(extension)) {
            insertTI89ImageFile(frame, area, f);
        } else {
            Image inputImage = null;
            final MediaTracker mt = new MediaTracker(frame);
            try {
                inputImage = loadInputImage(mt, Swinger.loadPicture(f));
            } catch (InvalidDataTypeException ex) {
                logger.warning(ex.getMessage());
                Swinger.showErrorDialog(frame, ex.getMessage());
            } catch (NotSupportedFileException ex) {
                logger.warning(ex.getMessage());
                Swinger.showErrorDialog(frame, ex.getMessage());
            } catch (IOException ex) {
                LogUtils.processException(logger, ex);
                Swinger.showErrorDialog(frame, ex.getMessage());
            }
            if (inputImage != null) {
                insertImageFromFilter(frame, area, f, inputImage);
                inputImage.flush();
            }
        }
    }
}