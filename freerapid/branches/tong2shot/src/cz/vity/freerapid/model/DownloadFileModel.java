package cz.vity.freerapid.model;

import com.sleepycat.persist.model.*;
import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.FileTypeIconProvider;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.core.tasks.DownloadTask;
import cz.vity.freerapid.gui.managers.interfaces.Identifiable;
import cz.vity.freerapid.gui.managers.interfaces.ModelWrapper;
import cz.vity.freerapid.model.bean.DownloadFile;
import cz.vity.freerapid.plugins.container.FileInfo;
import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;

import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Hashcode a Equals nepretizeny na url (muze byt vic souboru s touto url, neni unikatni),
 * pocita se s tim v ProcessManageru pri force download.
 *
 * @author Vity
 */

@Entity
public class DownloadFileModel implements Identifiable, HttpFile {
    private final static Logger logger = Logger.getLogger(DownloadFileModel.class.getName());

    @PrimaryKey(sequence = "ID")
    private Long dbId;
    private volatile long fileSize;
    private volatile DownloadState state = DownloadState.PAUSED;
    private volatile File storeFile;
    private String fileName;
    private volatile long downloaded = 0;
    private int sleep;
    private float averageSpeed;
    private volatile String errorMessage;
    private volatile URL fileUrl = null;
    private volatile File saveToDirectory;
    private volatile String description;
    private volatile String fileType;
    private volatile int timeToQueued = -1;
    private volatile int timeToQueuedMax = -1;
    private long completeTaskDuration = -1;
    private volatile int errorAttemptsCount;
    private volatile String shareDownloadServiceID;
    private volatile String serviceName = null;
    private volatile FileState fileState = FileState.NOT_CHECKED;
    private volatile Map<String, Object> properties = new Hashtable<String, Object>();
    private int speedLimit = -1;
    private volatile long realDownload;
    private volatile boolean resumeSupported = true;
    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
    private int listOrder;
    private Date dateInserted;
    private String localPluginConfig; //String representation of XMLEncoder output of plugin config
    private String fileNameRenameTo;
    private LocalConnectionSettingsType localConnectionSettingsType = LocalConnectionSettingsType.APPLICATION;
    private String localProxy;


    @NotPersistent
    private volatile DownloadTask task = null;
    @NotPersistent
    private volatile ConnectionSettings connectionSettings;
    @NotPersistent
    private float shortTimeAvgSpeed;
    @NotPersistent
    private long speed;
    @NotPersistent
    private volatile int tokens;
    @NotPersistent
    private int takenTokens;

    /**
     * Constructs a new DownloadFile.
     */
    public DownloadFileModel() {//XMLEncoder
        //logger.info("Konstruktor empty");
    }

    public DownloadFileModel(URL fileUrl, File saveToDirectory, String description) {
        this.fileUrl = fileUrl;
        this.saveToDirectory = saveToDirectory;
        this.description = description;
        setNewURL(fileUrl);
    }

    public DownloadFileModel(final FileInfo info, final File saveToDirectory) {
        setNewURL(info.getFileUrl());
        if (info.getFileName() != null) {
            this.fileName = info.getFileName();
        }
        this.fileSize = info.getFileSize();
        this.description = info.getDescription();
        this.saveToDirectory = saveToDirectory;
    }

    public FileInfo toFileInfo() {
        final FileInfo info = new FileInfo(fileUrl);
        info.setFileName(fileName);
        info.setFileSize(fileSize);
        info.setDescription(description);
        info.setSaveToDirectory(saveToDirectory.toString());
        return info;
    }

    /**
     * {@inheritDoc}
     */
    public void setNewURL(URL fileUrl) {
        setFileUrl(fileUrl);
        this.fileSize = -1;
        final String urlStr = fileUrl.toExternalForm();
        this.fileName = FileTypeIconProvider.identifyFileName(urlStr);
        //this.downloaded = 0;
        resetErrorAttempts();
        this.sleep = -1;
        this.averageSpeed = 0;
        this.speed = 0;
        this.resumeSupported = true;
        this.fileState = FileState.NOT_CHECKED;
        this.timeToQueued = -1;
        setFileType(FileTypeIconProvider.identifyFileType(fileName));
    }

    @Override
    public File getSaveToDirectory() {
        return saveToDirectory;
    }

    /**
     * {@inheritDoc}
     */
    public long getFileSize() {
        return fileSize;
    }

    /**
     * {@inheritDoc}
     */
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * Getter for property 'task'.
     *
     * @return Value for property 'task'.
     */
    public DownloadTask getTask() {
        return task;
    }

    /**
     * Setter for property 'task'.
     *
     * @param task Value to set for property 'task'.
     */
    public void setTask(DownloadTask task) {
        this.task = task;
    }

    /**
     * {@inheritDoc}
     */
    public DownloadState getState() {
        return state;
    }

    /**
     * {@inheritDoc}
     */
    public void setState(DownloadState state) {
        if (this.state == DownloadState.DELETED)
            return;
        this.state = state;
    }

    /**
     * {@inheritDoc}
     */
    public URL getFileUrl() {
        return fileUrl;
    }


    /**
     * {@inheritDoc}
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * {@inheritDoc}
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
        this.setFileType(FileTypeIconProvider.identifyFileType(this.fileName));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return fileUrl.toString();
    }

    /**
     * {@inheritDoc}
     */
    public long getDownloaded() {
        return downloaded;
    }

    /**
     * {@inheritDoc}
     */
    public void setDownloaded(long downloaded) {
        this.downloaded = downloaded;
    }

    /**
     * Setter for property 'speed'.
     *
     * @param speed Value to set for property 'speed'.
     */
    public void setSpeed(long speed) {
        this.speed = speed;
    }

    /**
     * Getter for property 'speed'.
     *
     * @return Value for property 'speed'.
     */
    public long getSpeed() {
        return speed;
    }

    /**
     * Setter for property 'sleep'.
     *
     * @param sleep Value to set for property 'sleep'.
     */
    public void setSleep(int sleep) {
        this.sleep = sleep;
    }

    /**
     * Setter for property 'averageSpeed'.
     *
     * @param averageSpeed Value to set for property 'averageSpeed'.
     */
    public void setAverageSpeed(float averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    /**
     * Setter for property 'errorMessage'.
     *
     * @param errorMessage Value to set for property 'errorMessage'.
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Getter for property 'sleep'.
     *
     * @return Value for property 'sleep'.
     */
    public int getSleep() {
        return sleep;
    }

    /**
     * Getter for property 'averageSpeed'.
     *
     * @return Value for property 'averageSpeed'.
     */
    public float getAverageSpeed() {
        return averageSpeed;
    }

    /**
     * Getter for property 'serviceName'.
     *
     * @return Value for property 'serviceName'.
     */
    public String getServiceName() {
        if (serviceName == null) {
            return "";
        } else return serviceName;
    }

    /**
     * Setter for property 'serviceName'.
     *
     * @param serviceName Value to set for property 'serviceName'.
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * Getter for property 'errorMessage'.
     *
     * @return Value for property 'errorMessage'.
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Getter for property 'outputFile'.
     *
     * @return Value for property 'outputFile'.
     */
    public File getOutputFile() {
        return new File(this.getSaveToDirectory(), (fileNameRenameTo != null ? fileNameRenameTo : fileName));
    }

    /**
     * Setter for property 'fileUrl'.
     *
     * @param fileUrl Value to set for property 'fileUrl'.
     */
    public void setFileUrl(URL fileUrl) {
        this.fileUrl = fileUrl;
    }

    /**
     * Setter for property 'saveToDirectory'.
     *
     * @param saveToDirectory Value to set for property 'saveToDirectory'.
     */
    public void setSaveToDirectory(File saveToDirectory) {
        this.saveToDirectory = saveToDirectory;
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter for property 'fileType'.
     *
     * @return Value for property 'fileType'.
     */
    public String getFileType() {
        return fileType;
    }

    /**
     * Setter for property 'fileType'.
     *
     * @param fileType Value to set for property 'fileType'.
     */
    public void setFileType(String fileType) {
        //String oldValue = this.fileType;
        this.fileType = fileType;
        //firePropertyChange("fileType", oldValue, this.fileType);
    }

    /**
     * Getter for property 'shareDownloadServiceID'.
     *
     * @return Value for property 'shareDownloadServiceID'.
     */
    @Deprecated
    public String getShareDownloadServiceID() {
        return shareDownloadServiceID;
    }

    /**
     * Setter for property 'timeToQueued'.
     *
     * @param i Value to set for property 'timeToQueued'.
     */
    public void setTimeToQueued(int i) {
        this.timeToQueued = i;
    }

    /**
     * Getter for property 'timeToQueued'.
     *
     * @return Value for property 'timeToQueued'.
     */
    public int getTimeToQueued() {
        return timeToQueued;
    }

    /**
     * Setter for property 'errorAttemptsCount'.
     *
     * @param errorAttemptsCount Value to set for property 'errorAttemptsCount'.
     */
    public void setErrorAttemptsCount(int errorAttemptsCount) {
        this.errorAttemptsCount = errorAttemptsCount;
    }

    /**
     * Getter for property 'errorAttemptsCount'.
     *
     * @return Value for property 'errorAttemptsCount'.
     */
    public int getErrorAttemptsCount() {
        return errorAttemptsCount;
    }

    public void resetErrorAttempts() {
        this.errorAttemptsCount = AppPrefs.getProperty(UserProp.ERROR_ATTEMPTS_COUNT, UserProp.ERROR_ATTEMPTS_COUNT_DEFAULT);
    }

    /**
     * Setter for property 'shareDownloadServiceID'.
     *
     * @param shareDownloadServiceID Value to set for property 'shareDownloadServiceID'.
     */
    @Deprecated
    public void setShareDownloadServiceID(String shareDownloadServiceID) {
        setPluginID(shareDownloadServiceID);
    }

    /**
     * Getter for property 'timeToQueuedMax'.
     *
     * @return Value for property 'timeToQueuedMax'.
     */
    public int getTimeToQueuedMax() {
        return timeToQueuedMax;
    }

    /**
     * Setter for property 'timeToQueuedMax'.
     *
     * @param timeToQueuedMax Value to set for property 'timeToQueuedMax'.
     */
    public void setTimeToQueuedMax(int timeToQueuedMax) {
        this.timeToQueuedMax = timeToQueuedMax;
    }

    public void resetSpeed() {
        setSpeed(0);
        setAverageSpeed(0);
    }

    /**
     * Getter for property 'completeTaskDuration'.
     *
     * @return Value for property 'completeTaskDuration'.
     */
    public long getCompleteTaskDuration() {
        return completeTaskDuration;
    }

    /**
     * Setter for property 'completeTaskDuration'.
     *
     * @param completeTaskDuration Value to set for property 'completeTaskDuration'.
     */
    public void setCompleteTaskDuration(final long completeTaskDuration) {
        this.completeTaskDuration = completeTaskDuration;
    }

    /**
     * Getter for property 'connectionSettings'.
     *
     * @return Value for property 'connectionSettings'.
     */
    public ConnectionSettings getConnectionSettings() {
        return connectionSettings;
    }

    /**
     * Setter for property 'connectionSettings'.
     *
     * @param connectionSettings Value to set for property 'connectionSettings'.
     */
    public void setConnectionSettings(final ConnectionSettings connectionSettings) {
        this.connectionSettings = connectionSettings;
    }

    /**
     * {@inheritDoc}
     */
    public FileState getFileState() {
        return fileState;
    }

    /**
     * {@inheritDoc}
     */
    public void setFileState(FileState fileState) {
        this.fileState = fileState;

    }

    public Map<String, Object> getProperties() {
        return this.properties;
    }

    /**
     * Setter for property 'properties'.
     *
     * @param properties Value to set for property 'properties'.
     */
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    /**
     * {@inheritDoc}
     */
    public void setPluginID(String pluginID) {
        this.shareDownloadServiceID = pluginID;
        this.serviceName = shareDownloadServiceID.toLowerCase().replace('_', ' ');
    }

    /**
     * {@inheritDoc}
     */
    public String getPluginID() {
        return this.shareDownloadServiceID;
    }

    public float getShortTimeAvgSpeed() {
        return shortTimeAvgSpeed;
    }

    public void setShortTimeAvgSpeed(final float shortTimeAvgSpeed) {
        this.shortTimeAvgSpeed = shortTimeAvgSpeed;
    }

    public int getSpeedLimit() {
        return speedLimit;
    }

    public void setSpeedLimit(final int speedLimit) {
        this.speedLimit = speedLimit;
    }

    public boolean hasSpeedLimit() {
        return speedLimit > 0;
    }

    public void setTokensLimit(int tokens) {
        this.tokens = tokens;
    }

    public int getTokensLimit() {
        return this.tokens;
    }

    public int getTakenTokens() {
        return takenTokens;
    }

    public void setTakenTokens(int takenTokens) {
        this.takenTokens = takenTokens;
    }

    @Override
    public File getStoreFile() {
//        if (this.storeFile == null) {
//            if (fileName != null)
//                return getOutputFile();
//        }
        return this.storeFile;
    }

    public int getTokens() {
        return tokens;
    }

    public void setTokens(int tokens) {
        this.tokens = tokens;
    }

    @Override
    public void setStoreFile(File storeFile) {
        this.storeFile = storeFile;
    }

    public long getRealDownload() {
        return realDownload;
    }

    public void setRealDownload(long realDownload) {
        this.realDownload = realDownload;
    }

    public boolean isResumeSupported() {
        return resumeSupported;
    }

    public Date getDateInserted() {
        return dateInserted;
    }

    public void setDateInserted(Date dateInserted) {
        this.dateInserted = dateInserted;
    }

    public void setResumeSupported(boolean resumeSupported) {
        this.resumeSupported = resumeSupported;
    }

    @Override
    public Long getIdentificator() {
        return dbId;
    }

    @Override
    public ModelWrapper build() {
        return new DownloadFile(this);
    }

    public int getListOrder() {
        return listOrder;
    }

    public void setListOrder(int listOrder) {
        this.listOrder = listOrder;
    }

    @Override
    public String getLocalPluginConfig() {
        return localPluginConfig;
    }

    @Override
    public void setLocalPluginConfig(String localPluginConfig) {
        this.localPluginConfig = localPluginConfig;
    }

    public String getFileNameRenameTo() {
        return fileNameRenameTo;
    }

    public void setFileNameRenameTo(String fileNameRenameTo) {
        this.fileNameRenameTo = fileNameRenameTo;
        this.setFileType(FileTypeIconProvider.identifyFileType(this.fileNameRenameTo));
    }

    public LocalConnectionSettingsType getLocalConnectionSettingsType() {
        return localConnectionSettingsType;
    }

    public void setLocalConnectionSettingsType(LocalConnectionSettingsType localConnectionSettingsType) {
        this.localConnectionSettingsType = localConnectionSettingsType;
    }

    public String getLocalProxy() {
        return localProxy;
    }

    public void setLocalProxy(String localProxy) {
        this.localProxy = localProxy;
    }
}
