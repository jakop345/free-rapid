package cz.vity.freerapid.gui.managers;

import com.jgoodies.binding.list.ArrayListModel;
import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.core.tasks.DownloadTask;
import cz.vity.freerapid.core.tasks.DownloadTaskError;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.plugins.exceptions.NotSupportedDownloadServiceException;
import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.HttpDownloadClient;
import cz.vity.freerapid.plugins.webclient.ShareDownloadService;
import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.TaskEvent;
import org.jdesktop.application.TaskListener;
import org.jdesktop.application.TaskService;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class ProcessManager extends Thread {
    private final static Logger logger = Logger.getLogger(ProcessManager.class.getName());
    private final ApplicationContext context;
    private DataManager dataManager;

    private volatile Stack<HttpDownloadClient> workingClients;
    private volatile Map<String, DownloadService> services = new Hashtable<String, DownloadService>();
    private volatile Map<DownloadFile, ConnectionSettings> forceDownloadFiles = new Hashtable<DownloadFile, ConnectionSettings>();

    private boolean threadSuspended;
    //private final Object queueLock;
    private final Object manipulation = new Object();
    private List<ConnectionSettings> availableConnections;
    private final java.util.Timer errorTimer = new java.util.Timer();
    private PluginsManager pluginsManager;


    public ProcessManager(ManagerDirector director, ApplicationContext context) {
        dataManager = director.getDataManager();
        pluginsManager = director.getPluginsManager();
        //  queueLock = dataManager.getLock();
        this.context = context;
        final ClientManager clientManager = director.getClientManager();
        availableConnections = clientManager.getAvailableConnections();
        this.workingClients = new Stack<HttpDownloadClient>();
        this.workingClients.addAll(clientManager.getClients());
        this.setName("ProcessManagerThread");
        initTaskService(context);
    }

    private void initTaskService(ApplicationContext context) {
        final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                AppPrefs.getProperty(UserProp.MAX_DOWNLOADS_AT_A_TIME, UserProp.MAX_DOWNLOADS_AT_A_TIME_DEFAULT),   // corePool size
                10,  // maximumPool size
                5L, TimeUnit.SECONDS,  // non-core threads time to live
                new LinkedBlockingQueue<Runnable>(10));
        final TaskService service = new TaskService("downloadService", threadPool);
        context.addTaskService(service);
    }


    @Override
    public void run() {
        while (!isInterrupted()) {
            synchronized (manipulation) {
                if (canCreateAnotherConnection() && !forceDownloadFiles.isEmpty())
                    execute(getFilesForForceDownload(), true);
                if (canCreateAnotherConnection())
                    execute(getQueued(), false);
            }
            try {
                synchronized (this) {
                    threadSuspended = true;
                    logger.info("Test for sleeping");
                    while (threadSuspended) {
                        wait();
                    }
                }
            } catch (InterruptedException e) {
                //ignore
            }
        }
    }

    private boolean canCreateAnotherConnection() {
        return !this.workingClients.isEmpty();
    }

    private LinkedList<DownloadFile> getFilesForForceDownload() {
        return new LinkedList<DownloadFile>(forceDownloadFiles.keySet());
    }

    private boolean execute(Collection<DownloadFile> files, boolean forceDownload) {
        for (DownloadFile file : files) {
            logger.info("Getting downloadFile " + file);
            final String serviceID = file.getShareDownloadServiceID();
            final ShareDownloadService service;
            try {
                service = pluginsManager.getPlugin(serviceID);
            } catch (NotSupportedDownloadServiceException e) {
                file.setState(DownloadState.ERROR);
                file.setErrorMessage("Not supported service - " + serviceID);
                continue;
            }
            DownloadService downloadService = services.get(serviceID);
            if (downloadService == null) {
                downloadService = new DownloadService(service);
                services.put(serviceID, downloadService);
            }

            if (!forceDownload) {
                for (ConnectionSettings settings : availableConnections) {
                    if (!settings.isEnabled())
                        continue;
                    if (downloadService.canDownloadWith(settings)) {
                        queueDownload(file, settings, downloadService, service);
                        break;
                    }
                }
            } else {
                if (!forceDownloadFiles.containsKey(file)) {
                    throw new IllegalStateException("Cannot find forceDownloaded File");
                } else {
                    final ConnectionSettings settings = forceDownloadFiles.remove(file);
                    queueDownload(file, settings, downloadService, service);
                }
            }
            if (!canCreateAnotherConnection())
                return true;
        }
        return false;
    }

    public void forceDownload(ConnectionSettings settings, List<DownloadFile> files) {
        synchronized (manipulation) {
            for (DownloadFile file : files) {
                if (!DownloadState.isProcessState(file.getState())) {
                    file.setState(DownloadState.QUEUED);
                    forceDownloadFiles.put(file, settings);
                }
            }
        }
        queueUpdated();
    }

    private void queueDownload(final DownloadFile downloadFile, final ConnectionSettings settings, DownloadService downloadService, final ShareDownloadService service) {
        final HttpDownloadClient client = workingClients.pop();
        client.initClient(settings);
        downloadService.addDownloadingClient(client);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (downloadFile.getState() != DownloadState.QUEUED)
                    finishedDownloading(downloadFile, client);
                else
                    startDownload(downloadFile, client, service);
            }
        });
    }

    private List<DownloadFile> getQueued() {
        //synchronized (queueLock) {
        final ArrayListModel<DownloadFile> files = dataManager.getDownloadFiles();
        final List<DownloadFile> queued = new LinkedList<DownloadFile>();

        final DownloadFile[] f = files.toArray(new DownloadFile[files.size()]);
        for (int i = f.length - 1; i >= 0; i--) {
            DownloadFile downloadFile = f[i];
            if (downloadFile.getState() == DownloadState.QUEUED) {
                queued.add(downloadFile);
            }
        }
        return queued;
        //}
    }

    private void startDownload(final DownloadFile downloadFile, final HttpDownloadClient client, ShareDownloadService service) {
        final DownloadState s = downloadFile.getState();
        logger.info("starting download in state s = " + s);
        try {
            final DownloadTask task = new DownloadTask(context.getApplication(), client, downloadFile, service);
            downloadFile.setTask(task);
            task.addTaskListener(new TaskListener.Adapter<Void, Long>() {

                @Override
                public void finished(TaskEvent<Void> event) {
                    finishedDownloading(downloadFile, client);
                    downloadFile.setTask(null);
                }
            });
            this.context.getTaskService("downloadService").execute(task);
        } catch (NotSupportedDownloadServiceException e) {
            LogUtils.processException(logger, e);
        }
    }

    private void finishedDownloading(final DownloadFile file, final HttpDownloadClient client) {
        synchronized (manipulation) {
            final String serviceName = file.getShareDownloadServiceID();
            final DownloadService service = services.get(serviceName);
            if (service == null)
                throw new IllegalStateException("Download service not found:" + serviceName);
            service.finishedDownloading(client);
            this.workingClients.add(client);
            int errorAttemptsCount = file.getErrorAttemptsCount();
            if (file.getState() == DownloadState.ERROR && errorAttemptsCount > 0) {
                final DownloadTask task = file.getTask();
                final DownloadTaskError error = task.getServiceError();
                if (error == DownloadTaskError.NOT_RECOVERABLE_DOWNLOAD_ERROR) {
                    file.setErrorAttemptsCount(0);
                } else {
                    file.setErrorAttemptsCount(--errorAttemptsCount);
                    final ConnectionSettings settings = client.getSettings();
                    service.addProblematicConnection(settings);
                    if (error == DownloadTaskError.YOU_HAVE_TO_WAIT_ERROR) {
                        int waitTime = task.getYouHaveToSleepSecondsTime();
                        errorTimer.schedule(new ErrorTimerTask(service, settings, file, waitTime), 0, 1000);
                    } else
                        errorTimer.schedule(new ErrorTimerTask(service, settings, file), 0, 1000);
                }
            }
        }
        wakeUp();
    }

    public void queueUpdated() {
        wakeUp();
    }

    private void wakeUp() {
        synchronized (this) {
            threadSuspended = false;
            this.notify();
        }
    }

    private class ErrorTimerTask extends TimerTask {
        private int counter;
        private final DownloadService service;
        private final ConnectionSettings settings;
        private final DownloadFile file;

        public ErrorTimerTask(DownloadService service, ConnectionSettings settings, DownloadFile file) {
            this(service, settings, file, AppPrefs.getProperty(UserProp.AUTO_RECONNECT_TIME, UserProp.AUTO_RECONNECT_TIME_DEFAULT));
        }

        public ErrorTimerTask(DownloadService service, ConnectionSettings settings, DownloadFile file, int waitTime) {
            this.service = service;
            this.settings = settings;
            this.file = file;
            this.counter = waitTime;
            file.setTimeToQueuedMax(waitTime);
        }

        public void run() {
            if (file.getState() != DownloadState.ERROR) { //doslo ke zmene stavu z venci
                this.cancel(); //zrusime timer
                file.setTimeToQueued(-1); //odecitani casu
                file.setTimeToQueuedMax(-1);
                renewProblematicConnection();
                file.resetErrorAttempts(); //je nutne vyresetovat pocet error pokusu
                return;
            }
            file.setTimeToQueued(--counter); //normalni prubeh, jeden tick
            if (counter <= 0) { //zarazeni zpatky do fronty
                file.setTimeToQueued(-1);
                file.setTimeToQueuedMax(-1);
                renewProblematicConnection();
                file.setState(DownloadState.QUEUED);
                this.cancel();
                queueUpdated();
            }
        }

        private void renewProblematicConnection() {
            service.removeProblematicConnection(settings);
        }
    }

}