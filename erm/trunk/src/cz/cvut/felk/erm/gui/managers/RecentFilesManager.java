package cz.cvut.felk.erm.gui.managers;

import cz.cvut.felk.erm.core.AppPrefs;
import cz.cvut.felk.erm.core.Consts;
import cz.cvut.felk.erm.core.UserProp;
import cz.cvut.felk.erm.gui.managers.interfaces.IFileChangeListener;
import cz.cvut.felk.erm.gui.managers.interfaces.IFileInstance;

import java.io.File;
import java.util.Iterator;
import java.util.Stack;

/**
 * @author Ladislav Vitasek
 */
public final class RecentFilesManager implements IFileChangeListener {
    private final MenuManager menuManager;
    private final Stack<File> recentFilesList = new Stack<File>();
    private static final String RECENT_PREFERENCES = "recent";

    public RecentFilesManager(final MenuManager menuManager) {
        this.menuManager = menuManager;
        loadRecentFiles();
    }

    private static int getMaxRecentFilesSettings() {
        return AppPrefs.getProperty(UserProp.MAX_RECENT_FILES, Consts.DEFAULT_RECENT_FILES_MAX_COUNT);
    }

    private void loadRecentFiles() {
        String fileName, key;
        for (int counter = 0; (fileName = AppPrefs.getProperty(key = RECENT_PREFERENCES + counter, null)) != null; ++counter)
        {
            if (fileName.length() > 0) {
                recentFilesList.add(0, new File(fileName));
                AppPrefs.removeProperty(key);
            }
        }
        final int maxRecents = getMaxRecentFilesSettings();
        if (recentFilesList.size() > maxRecents)
            recentFilesList.setSize(maxRecents);
        if (!recentFilesList.isEmpty())
            buildRecentFileMenu();
    }

    private void fileWasOpened(final File file) {
        if (file == null)
            return;
        final int found = recentFilesList.indexOf(file);
        if (found != -1) {
            //found
            recentFilesList.remove(found);
            buildRecentFileMenu();
        }
    }


    public void fileWasOpened(final FileChangeEvent event) {
        fileWasOpened(event.getFileInstance().getFile());
    }

    public void fileWasClosed(final FileChangeEvent event) {
        final IFileInstance fileInstance = event.getFileInstance();        
        if (!fileInstance.hasAssignedFile())
            return;
        if (recentFilesList.indexOf(fileInstance.getFile()) < 0) {
            //found
            recentFilesList.add(0, fileInstance.getFile());
            final int maxRecents = getMaxRecentFilesSettings();
            if (recentFilesList.size() > maxRecents)
                recentFilesList.setSize(maxRecents);
            buildRecentFileMenu();
        }
    }

    public void buildRecentFileMenu() {
        menuManager.updateRecentMenu(recentFilesList);
    }

    public final void removeBadFile(final File file) {
        fileWasOpened(file);
    }

    public final void storeRecentFiles() {
        int counter = recentFilesList.size() - 1;
        for (final Iterator<File> it = recentFilesList.iterator(); it.hasNext(); --counter) {
            AppPrefs.storeProperty(RECENT_PREFERENCES + counter, it.next().toString());
        }
    }
}
