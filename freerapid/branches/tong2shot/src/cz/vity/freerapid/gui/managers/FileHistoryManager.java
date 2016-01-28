package cz.vity.freerapid.gui.managers;


import com.jgoodies.common.collect.ArrayListModel;
import cz.vity.freerapid.model.FileHistoryItemModel;
import cz.vity.freerapid.model.bean.DownloadFile;
import cz.vity.freerapid.utilities.FileUtils;
import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.application.AbstractBean;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.LocalStorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class FileHistoryManager extends AbstractBean {
    private final static Logger logger = Logger.getLogger(FileHistoryManager.class.getName());

    private final ManagerDirector director;
    private final ApplicationContext context;

    //    private boolean loaded = false;
    private static final String FILES_LIST_XML = "history.xml";

    //   private int dataChanged = 0;

    public FileHistoryManager(ManagerDirector director, ApplicationContext context) {
        this.director = director;
        this.context = context;
        init();
    }

    private void init() {

    }

    @SuppressWarnings({"unchecked"})
    private List<FileHistoryItemModel> loadList(final File srcFile) throws IOException {
        final List<FileHistoryItemModel> list = new LinkedList<FileHistoryItemModel>();
        final LocalStorage localStorage = context.getLocalStorage();
        if (!srcFile.exists()) { //extract from old file, we ignore existence of backup file in case the main file does not exist
            final File backupFile = FileUtils.getBackupFile(srcFile);
            if (backupFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                backupFile.renameTo(srcFile);
            }
        }


        if (!srcFile.exists()) {
            return list;
        }

        final Object o = localStorage.load(FILES_LIST_XML);

        if (o instanceof ArrayListModel) {
            return (List<FileHistoryItemModel>) o;
        }
        return list;
    }

    public Collection<FileHistoryItemModel> getItems() {
        return loadFileHistoryList();
    }

    private Collection<FileHistoryItemModel> loadFileHistoryList() {
        List<FileHistoryItemModel> result = null;
        final File srcFile = new File(context.getLocalStorage().getDirectory(), FILES_LIST_XML);
        final File targetImportedFile = new File(context.getLocalStorage().getDirectory(), FILES_LIST_XML + ".imported");
        if (srcFile.exists() && !targetImportedFile.exists()) { //extract from old file
            try {
                result = loadList(srcFile);
            } catch (Exception e) {
                LogUtils.processException(logger, e);
                logger.info("Trying to renew file from backup");
                try {
                    FileUtils.renewBackup(srcFile);
                    result = loadList(srcFile);
                } catch (FileNotFoundException ex) {
                    //ignore
                } catch (Exception e1) {
                    LogUtils.processException(logger, e);
                }
            }
            if (result != null) {
                //re-save into database
                final List<FileHistoryItemModel> finalResult = result;
                Runnable runnable = new Runnable() {
                    public void run() {
                        logger.info("Resaving file history into database from old format");
                        director.getDatabaseManager().saveCollection(finalResult, FileHistoryItemModel.class);
                    }
                };
                director.getDatabaseManager().runOnTask(runnable);
            } else result = new ArrayList<FileHistoryItemModel>();
            //rename old file history file into another one, so we won't import it again next time
            //noinspection ResultOfMethodCallIgnored
            final File backupFile = FileUtils.getBackupFile(srcFile);
            if (backupFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                backupFile.renameTo(new File(context.getLocalStorage().getDirectory(), FILES_LIST_XML + ".backup.imported"));
            }
            srcFile.renameTo(targetImportedFile);
            return result;
        } else {
            //load from database
            return director.getDatabaseManager().loadAll(FileHistoryItemModel.class);
        }
    }

    public void addHistoryItem(final DownloadFile file, final File savedAs) {
        final FileHistoryItemModel item = new FileHistoryItemModel(file, savedAs);
        Runnable runnable = new Runnable() {
            public void run() {
                director.getDatabaseManager().saveOrUpdate(item, FileHistoryItemModel.class);
            }
        };
        director.getDatabaseManager().runOnTask(runnable);
        fireDataAdded(item);
    }

    public void clearHistory(Runnable succeeded) {
        Runnable runnable = new Runnable() {
            public void run() {
                director.getDatabaseManager().removeAll(FileHistoryItemModel.class);
            }
        };
        director.getDatabaseManager().runOnTask(runnable, succeeded);
    }


    public void removeItems(final Collection<FileHistoryItemModel> items) {
        Runnable runnable = new Runnable() {
            public void run() {
                director.getDatabaseManager().removeCollection(items, FileHistoryItemModel.class);
            }
        };
        director.getDatabaseManager().runOnTask(runnable, null);
    }

    public void removeItems(final Collection<FileHistoryItemModel> items, Runnable succeeded) {
        Runnable runnable = new Runnable() {
            public void run() {
                director.getDatabaseManager().removeCollection(items, FileHistoryItemModel.class);
            }
        };
        director.getDatabaseManager().runOnTask(runnable, succeeded);
    }


    private void fireDataAdded(FileHistoryItemModel dataAdded) {
        firePropertyChange("dataAdded", null, dataAdded);
    }


//
//
}
