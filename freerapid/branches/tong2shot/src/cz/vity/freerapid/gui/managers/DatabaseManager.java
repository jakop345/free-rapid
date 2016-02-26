package cz.vity.freerapid.gui.managers;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.Transaction;
import com.sleepycat.persist.*;
import com.sleepycat.persist.model.AnnotationModel;
import com.sleepycat.persist.model.EntityModel;
import cz.vity.freerapid.core.tasks.CoreTask;
import cz.vity.freerapid.gui.managers.interfaces.Identifiable;
import cz.vity.freerapid.gui.managers.interfaces.ModelWrapper;
import cz.vity.freerapid.model.*;
import cz.vity.freerapid.model.bean.DownloadFile;
import cz.vity.freerapid.model.proxy.FileProxy;
import cz.vity.freerapid.model.proxy.HashTableProxy;
import cz.vity.freerapid.model.proxy.URLProxy;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.application.TaskService;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class DatabaseManager {
    private final static Logger logger = Logger.getLogger(DatabaseManager.class.getName());
    private final ManagerDirector director;
    private final Environment myEnv;
    private final EntityStore store;

    private final PrimaryIndex<Long, DownloadFileModel> downloadFileById;
    private final SecondaryIndex<String, Long, DownloadFileModel> downloadFileByListOrder;
    private final PrimaryIndex<Long, PluginMetaDataModel> pluginMetaDataById;
    private final PrimaryIndex<Long, FileHistoryItemModel> fileHistoryById;
    private final PrimaryIndex<Long, ProxySetModel> proxySetById;
    private final PrimaryIndex<Long, ProxyForPluginModel> proxyForPluginById;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public DatabaseManager(ManagerDirector director) {
        this.director = director;
        final File envHome = new File(director.getContext().getLocalStorage().getDirectory(), "bdb");
        envHome.mkdirs();

        EntityModel model = new AnnotationModel();
        model.registerClass(FileProxy.class);
        model.registerClass(HashTableProxy.class);
        model.registerClass(URLProxy.class);

        EnvironmentConfig myEnvConfig = new EnvironmentConfig();
        StoreConfig storeConfig = new StoreConfig();

        storeConfig.setModel(model);

        myEnvConfig.setReadOnly(false);
        storeConfig.setReadOnly(false);
        myEnvConfig.setAllowCreate(true);
        storeConfig.setAllowCreate(true);
        myEnvConfig.setTransactional(true);
        storeConfig.setTransactional(true);
        myEnvConfig.setConfigParam(EnvironmentConfig.STATS_COLLECT, "false");

        myEnv = new Environment(envHome, myEnvConfig);
        store = new EntityStore(myEnv, "EntityStore", storeConfig);

        downloadFileById = store.getPrimaryIndex(Long.class, DownloadFileModel.class);
        downloadFileByListOrder = store.getSecondaryIndex(downloadFileById, String.class, "listOrder");
        pluginMetaDataById = store.getPrimaryIndex(Long.class, PluginMetaDataModel.class);
        fileHistoryById = store.getPrimaryIndex(Long.class, FileHistoryItemModel.class);
        proxySetById = store.getPrimaryIndex(Long.class, ProxySetModel.class);
        proxyForPluginById = store.getPrimaryIndex(Long.class, ProxyForPluginModel.class);

        logger.info("Database path " + envHome);
        //hook to shutdown database on JVM close
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                close();
            }
        }));
    }

    // Return a handle to the entity store
    public EntityStore getEntityStore() {
        return store;
    }

    // Return a handle to the environment
    public Environment getEnv() {
        return myEnv;
    }

    // Close the store and environment
    public void close() {
        if (store != null) {
            try {
                store.close();
            } catch (DatabaseException dbe) {
                LogUtils.processException(logger, dbe);
            }
        }

        if (myEnv != null) {
            try {
                // Finally, close the store and environment.
                myEnv.cleanLog();
                myEnv.close();
            } catch (DatabaseException dbe) {
                LogUtils.processException(logger, dbe);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Identifiable> PrimaryIndex<Long, T> getPrimaryIndex(Class<T> entityClass) {
        if (entityClass == DownloadFileModel.class) {
            return (PrimaryIndex<Long, T>) downloadFileById;
        }
        if (entityClass == PluginMetaDataModel.class) {
            return (PrimaryIndex<Long, T>) pluginMetaDataById;
        }
        if (entityClass == FileHistoryItemModel.class) {
            return (PrimaryIndex<Long, T>) fileHistoryById;
        }
        if (entityClass == ProxySetModel.class) {
            return (PrimaryIndex<Long, T>) proxySetById;
        }
        return (PrimaryIndex<Long, T>) proxyForPluginById;
    }

    @SuppressWarnings("unchecked")
    public synchronized <T extends Identifiable, B extends ModelWrapper> void saveCollection(Collection<B> beanCollection, Class<T> entityClass) {
        Transaction txn = myEnv.beginTransaction(null, null);
        PrimaryIndex<Long, T> primaryIndex = getPrimaryIndex(entityClass);
        try {
            for (B o : beanCollection) {
                primaryIndex.put(txn, (T) o.getModel());
            }
            txn.commit();
        } catch (Exception ex) {
            LogUtils.processException(logger, ex);
            if (txn != null) {
                txn.abort();
            }
        }
    }

    public synchronized <T extends Identifiable, B extends ModelWrapper> void removeCollection(Collection<B> beanCollection, Class<T> entityClass) {
        Transaction txn = myEnv.beginTransaction(null, null);
        PrimaryIndex<Long, T> primaryIndex = getPrimaryIndex(entityClass);
        try {
            for (B o : beanCollection) {
                if (o.getModel().getIdentificator() == null) {
                    continue;
                }
                primaryIndex.delete(txn, (Long) o.getModel().getIdentificator());
            }
            txn.commit();
        } catch (Exception ex) {
            LogUtils.processException(logger, ex);
            if (txn != null) {
                txn.abort();
            }
        }
    }

    public synchronized <T extends Identifiable> int removeAll(Class<T> entityClass) {
        int affectedResult = 0;
        Transaction txn = myEnv.beginTransaction(null, null);
        PrimaryIndex<Long, T> primaryIndex = getPrimaryIndex(entityClass);
        EntityCursor<T> entityCursor = primaryIndex.entities(txn, null);
        try {
            Iterator<T> iter = entityCursor.iterator();
            while (iter.hasNext()) {
                iter.next();
                iter.remove();
                affectedResult++;
            }
            entityCursor.close();
            txn.commit();
        } catch (Exception ex) {
            LogUtils.processException(logger, ex);
            if (txn != null) {
                txn.abort();
            }
        }
        return affectedResult;
    }

    @SuppressWarnings("unchecked")
    public synchronized <T extends Identifiable, B extends ModelWrapper> void saveOrUpdate(B bean, Class<T> entityClass) {
        Transaction txn = myEnv.beginTransaction(null, null);
        PrimaryIndex<Long, T> primaryIndex = getPrimaryIndex(entityClass);
        try {
            primaryIndex.put(txn, (T) bean.getModel());
            txn.commit();
        } catch (Exception ex) {
            LogUtils.processException(logger, ex);
            if (txn != null) {
                txn.abort();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public synchronized <T extends Identifiable, B extends ModelWrapper> Collection<B> loadAll(Class<T> entityClass) {
        PrimaryIndex<Long, T> primaryIndex = getPrimaryIndex(entityClass);
        Collection<B> results = new LinkedList<>();
        EntityCursor<T> entityCursor = primaryIndex.entities();
        try {
            for (T anEntityCursor : entityCursor) {
                results.add((B) anEntityCursor.build());
            }
            entityCursor.close();
        } catch (Exception ex) {
            LogUtils.processException(logger, ex);
        }
        return results;
    }

    /**
     * Load download file list from DB ordered by 'listOrder'
     *
     * @return download file list
     */
    public synchronized Collection<DownloadFile> loadAllOrderByListOrder() {
        Collection<DownloadFile> results = new LinkedList<>();
        EntityCursor<DownloadFileModel> entityCursor = downloadFileByListOrder.entities();
        try {
            for (DownloadFileModel anEntityCursor : entityCursor) {
                results.add((DownloadFile) anEntityCursor.build());
            }
            entityCursor.close();
        } catch (Exception ex) {
            LogUtils.processException(logger, ex);
        }
        return results;
    }

    public void runOnTask(final Runnable runnable) {
        runOnTask(runnable, null);
    }

    public void runOnTask(final Runnable runnable, final Runnable succeeded) {
        final TaskService service = director.getTaskServiceManager().getTaskService(TaskServiceManager.DATABASE_SERVICE);

        service.execute(new CoreTask(director.getContext().getApplication()) {
            @Override
            protected void succeeded(Object result) {
                if (succeeded != null) {
                    succeeded.run();
                }
            }

            @Override
            protected Object doInBackground() throws Exception {
                runnable.run();
                return null;
            }

            //TODO: fixme
            @Override
            protected void failed(Throwable cause) {
                LogUtils.processException(logger, cause);
                if (cause instanceof DatabaseException && cause
                        .getMessage().contains("error 141")) {
                    Swinger.showErrorDialog(director.getContext().getResourceMap(), "DatabaseManager.databaseIsAlreadyUsed", cause);
                } else super.failed(cause);
            }
        });
    }

}
