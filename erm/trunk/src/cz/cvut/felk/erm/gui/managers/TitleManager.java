package cz.cvut.felk.erm.gui.managers;

import cz.cvut.felk.erm.core.AppPrefs;
import cz.cvut.felk.erm.core.Consts;
import cz.cvut.felk.erm.core.UserProp;
import cz.cvut.felk.erm.gui.managers.interfaces.IAreaChangeListener;
import cz.cvut.felk.erm.gui.managers.interfaces.IFileInstance;
import cz.cvut.felk.erm.gui.managers.interfaces.InstanceListener;

import javax.swing.*;
import java.io.File;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
public class TitleManager implements IAreaChangeListener, InstanceListener {
    public static final int TITLE_FILENAME = 0;
    public static final int VARIABLE_NAME = 1;
    public static final int FOLDER_VARIABLE_NAME = 2;
    public static final int FILE_PATH = 3;
    private final static Logger logger = Logger.getLogger(TitleManager.class.getName());
    private static final String TITLE_FORMAT = "%s - [%s%s]";
    private final ManagerDirector director;

    public TitleManager(ManagerDirector director) {
        this.director = director;
    }

    public void areaActivated(AreaChangeEvent event) {
        event.getFileInstance().addInstanceListener(this);
        updateTitle(event.getFileInstance());
    }

    public void areaDeactivated(AreaChangeEvent event) {
        event.getFileInstance().removeInstanceListener(this);
        if (!((AreaManager) event.getSource()).hasOpenedInstance())
            setEmptyInfo();
    }


    public void updateTitle() {
        if (!AppPrefs.getProperty(UserProp.FRAME_TITLE, true))
            setEmptyInfo();
        else 
            updateTitle(director.getAreaManager().getActiveInstance());
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

        if (AppPrefs.getProperty(UserProp.FRAME_TITLE, true)) {
            final String tabName;
            File file = instance.getFile();
            switch (AppPrefs.getProperty(UserProp.FRAME_TITLE_TYPE, TITLE_FILENAME)) {
                case VARIABLE_NAME:
                    tabName = instance.getName();
                    break;
                case FILE_PATH:
                    if (instance.hasAssignedFile()) {
                        tabName = file.getPath();
                    } else tabName = instance.getName();
                    break;
                default:
                    tabName = instance.getName();
                    break;
            }
            setTitle(String.format(TITLE_FORMAT, Consts.APPVERSION, tabName, (instance.isModified()) ? "*" : ""));
        }
    }

    private void setEmptyInfo() {
        setTitle(Consts.APPVERSION);
    }

    private void setTitle(final String info) {
        final JFrame frame = director.getMainFrame();
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
