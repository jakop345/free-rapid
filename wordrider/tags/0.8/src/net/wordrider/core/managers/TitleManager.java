package net.wordrider.core.managers;

import net.wordrider.core.AppPrefs;
import net.wordrider.core.managers.interfaces.IAreaChangeListener;
import net.wordrider.core.managers.interfaces.IFileInstance;
import net.wordrider.core.managers.interfaces.InstanceListener;
import net.wordrider.utilities.Consts;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class TitleManager implements IAreaChangeListener, InstanceListener {
    public static final int TITLE_FILENAME = 0;
    public static final int VARIABLE_NAME = 1;
    public static final int FOLDER_VARIABLE_NAME = 2;
    public static final int FILE_PATH = 3;
    private final Frame frame;
    private final static Logger logger = Logger.getLogger(TitleManager.class.getName());
    private static final String TITLE_FORMAT = "%s - [%s%s]";

    public TitleManager(final Frame frame) {
        this.frame = frame;
    }

    public void areaActivated(AreaChangeEvent event) {
        event.getFileInstance().addInstanceListener(this);
        updateTitle(event.getFileInstance());
    }

    public void areaDeactivated(AreaChangeEvent event) {
        event.getFileInstance().removeInstanceListener(this);
        if (((AreaManager) event.getSource()).getOpenedInstanceCount() == 1)
            setEmptyInfo();
    }


    public void updateTitle() {
        if (!AppPrefs.getProperty(AppPrefs.FRAME_TITLE, true))
            setEmptyInfo();
        else 
            updateTitle(AreaManager.getInstance().getActiveInstance());
    }


    public void instanceModifiedStatusChanged(InstanceEvent e) {
        fileAssigned(e);
    }

    public void fileAssigned(InstanceEvent e) {
        updateTitle(e.getInstance());
    }

    private void updateTitle(final IFileInstance instance) {
        if (instance == null) {
            setEmptyInfo();
            return;
        }

        if (AppPrefs.getProperty(AppPrefs.FRAME_TITLE, true)) {
            final String tabName;
            switch (AppPrefs.getProperty(AppPrefs.FRAME_TITLE_TYPE, TITLE_FILENAME)) {
                case VARIABLE_NAME:
                    tabName = getVarName(instance);
                    break;
                case FOLDER_VARIABLE_NAME:
                    tabName = getFolderName(instance) + "/" + getVarName(instance);
                    break;
                case FILE_PATH:
                    if (instance.hasAssignedFile()) {
                        tabName = instance.getFile().getPath();     
                    } else tabName = instance.getName();
                    break;
                default:
                    tabName = instance.getName();
                    break;
            }
            setTitle(String.format(TITLE_FORMAT, Consts.APPVERSION, tabName, (instance.isModified()) ? "*" : ""));
        }
    }

    private static String getVarName(IFileInstance instance) {
        String name = instance.getFileInfo().getVarName();
        if (name.length() == 0)
            name = AppPrefs.getProperty(AppPrefs.DEFAULT_VARIABLE, Consts.DEFAULT_VARNAME);
        return name;
    }

    private static String getFolderName(IFileInstance instance) {
        String name = instance.getFileInfo().getFolderName();
        if (name.length() == 0)
            name = AppPrefs.getProperty(AppPrefs.DEFAULT_FOLDER, Consts.DEFAULT_FOLDERNAME);
        return name;
    }

    private void setEmptyInfo() {
        setTitle(Consts.APPVERSION);
    }

    private void setTitle(final String info) {
        if (!frame.getTitle().equals(info)) {
            logger.info("Updating title to " + info);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    frame.setTitle(info);
                }
            });

        }
    }

}
