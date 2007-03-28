package net.wordrider.core.actions;

import net.wordrider.area.AreaImage;
import net.wordrider.area.RiderArea;
import net.wordrider.core.AppPrefs;
import net.wordrider.core.Lng;
import net.wordrider.core.managers.AreaManager;
import net.wordrider.core.managers.interfaces.IFileInstance;
import net.wordrider.dialogs.SaveSettingsDialog;
import net.wordrider.files.ti68kformat.TIFileInfo;
import net.wordrider.files.ti68kformat.TIImageUpdater;
import net.wordrider.utilities.Swinger;
import net.wordrider.utilities.Utils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public final class ChangeImagePropertiesAction extends CoreAction {
    private static final ChangeImagePropertiesAction instance = new ChangeImagePropertiesAction();
    //  private static File currentDirectory;
    private static final String CODE = "ChangeImagePropertiesAction";
    private final static Logger logger = Logger.getLogger(ChangeImagePropertiesAction.class.getName());
    public static ChangeImagePropertiesAction getInstance() {
        return instance;
    }

    private ChangeImagePropertiesAction() {
        super(CODE, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.ALT_MASK), "ins_pic_v1.gif");
    }


    public final void actionPerformed(final ActionEvent e) {
        final JFrame frame = getMainFrame();
        final IFileInstance instance = AreaManager.getInstance().getActiveInstance();
        if (instance == null)
            return;
        final RiderArea area = (RiderArea) instance.getRiderArea();
        final AreaImage image = area.getSelectedImage();
        if (image == null)
            return;

        final TIFileInfo info = SaveAsFileAction.getSaveSettings(frame, image.getTIFileInfo(), SaveSettingsDialog.STATUS_IMAGE_IMAGEPROPERTIES);
        if (info == null)
            return;

        final File linkedFile = image.getOriginalFile();
        if (linkedFile == null) {
            image.setTIFileInfo(info);
            return;
        }
        final String previousVariableName = image.getTIFileInfo().getVarName();

        boolean renameFile = false;
        if (!previousVariableName.equals(info.getVarName())) {
            if (!(Utils.isWindows() && previousVariableName.equalsIgnoreCase(info.getVarName()))) {
                if (!AppPrefs.getProperty(AppPrefs.RENAME_IMAGE_AUTOMATICALLY, false)) {
                    final int result = Swinger.getChoiceCancel(frame, Lng.getLabel("message.confirm.variable"));
                    switch (result) {
                        case Swinger.RESULT_YES:
                            renameFile = true;
                            break;
                        case Swinger.RESULT_NO:
                            break;
                        default:
                            return;
                    }
                } else renameFile = true;
            }
        }

        try {
            final TIImageUpdater updater = new TIImageUpdater(info, linkedFile);
            File outputFile = updater.doUpdate(renameFile);
            image.setOriginalFile(outputFile);
            image.setTIFileInfo(info);
            instance.setModified(true);
        } catch (FileNotFoundException ex) {
            logger.warning("Image properties. File was not found : " + linkedFile);
            Swinger.showErrorDialog(frame, Lng.getLabel("message.error.FNF", linkedFile));
        } catch (IOException ex) {
            logger.warning("Image properties. Error working with a file : " + linkedFile + "\n" + ex.getMessage());
            Swinger.showErrorDialog(frame, ex.getMessage());
        } catch (IllegalAccessException ex) {
            Swinger.showErrorDialog(frame, Lng.getLabel("message.error.corruptedFile", linkedFile));
            logger.warning("Image properties. Corrupted file." + linkedFile);
        }
    }

    public void updateEnabled() {
        final IFileInstance instance = AreaManager.getInstance().getActiveInstance();
        if (instance != null)
            updateEnabled((RiderArea) instance.getRiderArea());
    }

    public void updateEnabled(RiderArea area) {
        setEnabled(area.getSelectedImage() != null);
    }


}
