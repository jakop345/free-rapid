package cz.cvut.felk.erm.gui.managers;

import cz.cvut.felk.erm.utilities.LogUtils;
import org.jdesktop.application.LocalStorage;
import org.noos.xing.mydoggy.ContentManagerUI;
import org.noos.xing.mydoggy.PersistenceDelegate;
import org.noos.xing.mydoggy.plaf.MyDoggyToolWindowManager;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
public class DockingManager {
    private final static Logger logger = Logger.getLogger(DockingManager.class.getName());


    private MyDoggyToolWindowManager toolsManager;
    private final ManagerDirector director;

    private final static String USER_LAYOUT_SESSION_FILENAME = "docking.session.xml";

    public DockingManager(ManagerDirector director, JFrame frame) {
        this.director = director;
        this.toolsManager = new MyDoggyToolWindowManager(frame);
        this.toolsManager.getContentManager().setContentManagerUI(new MyDesktopContentManagerUI());
    }

    public MyDoggyToolWindowManager getToolManager() {
        return toolsManager;
    }

    public void storeLayout() {
        try {
            final LocalStorage localStorage = director.getContext().getLocalStorage();
            final OutputStream outputStream = localStorage.openOutputFile(USER_LAYOUT_SESSION_FILENAME);
            final PersistenceDelegate persistenceDelegate = this.toolsManager.getPersistenceDelegate();
            persistenceDelegate.save(outputStream);
        } catch (IOException e) {
            LogUtils.processException(logger, e);
        }
    }

    public void renewLayout() {
        try {
            final LocalStorage localStorage = director.getContext().getLocalStorage();
            final File storageDir = localStorage.getDirectory();
            storageDir.mkdirs();
            final File userFile = new File(storageDir, USER_LAYOUT_SESSION_FILENAME);
            if (!(userFile.exists()))
                return;

            final InputStream inputStream = new FileInputStream(new File(storageDir, USER_LAYOUT_SESSION_FILENAME));
            final PersistenceDelegate persistenceDelegate = this.toolsManager.getPersistenceDelegate();
            persistenceDelegate.apply(inputStream);
        } catch (IOException e) {
            LogUtils.processException(logger, e);
        }
    }

    public ContentManagerUI getContentManagerUI() {
        return toolsManager.getContentManager().getContentManagerUI();
    }

    public Container getContentPane() {
        return ((MyDesktopContentManagerUI) toolsManager.getContentManager().getContentManagerUI()).getContainer();
    }
}
