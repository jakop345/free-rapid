package cz.vity.freerapid.model.bean;

import com.sleepycat.persist.model.Entity;
import cz.vity.freerapid.core.tasks.DownloadTask;
import cz.vity.freerapid.gui.managers.interfaces.ModelWrapper;
import cz.vity.freerapid.model.DownloadFileModel;
import cz.vity.freerapid.plugins.container.FileInfo;
import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import org.jdesktop.application.AbstractBean;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Hashcode a Equals nepretizeny na url (muze byt vic souboru s touto url, neni unikatni),
 * pocita se s tim v ProcessManageru pri force download.
 *
 * @author Vity
 */

@Entity
public class DownloadFile extends AbstractBean implements PropertyChangeListener, HttpFile, ModelWrapper {
    private final static Logger logger = Logger.getLogger(DownloadFile.class.getName());

    private DownloadFileModel model;

    public DownloadFile(DownloadFileModel model) {
        this.model = model;
    }

    @Override
    public DownloadFileModel getModel() {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void setFileSize(long fileSize) {
        long oldValue = model.getFileSize();
        model.setFileSize(fileSize);
        firePropertyChange("fileSize", oldValue, model.getFileSize());
    }

    /**
     * Setter for property 'task'.
     *
     * @param task Value to set for property 'task'.
     */
    public void setTask(DownloadTask task) {
        if (task == null) {
            if (model.getTask() != null)
                model.getTask().removePropertyChangeListener(this);
        } else {
            task.addPropertyChangeListener(this);
        }
        //System.out.println("task = " + task);
        model.setTask(task);
    }

    /**
     * {@inheritDoc}
     */
    public void setState(DownloadState state) {
        if (model.getState() == DownloadState.DELETED)
            return;
        DownloadState oldValue = model.getState();
        model.setState(state);
        if (oldValue != state)
            logger.info("Setting state to " + state.toString());
        firePropertyChange("state", oldValue, model.getState());
    }

    /**
     * {@inheritDoc}
     */
    public void setFileName(String fileName) {
        String oldValue = model.getFileName();
        model.setFileName(fileName);
        firePropertyChange("fileName", oldValue, model.getFileName());
    }

    /**
     * {@inheritDoc}
     */
    public void setDownloaded(long downloaded) {
        final long oldValue = model.getDownloaded();
        model.setDownloaded(downloaded);
        logger.fine("setting downloaded to " + downloaded);
        firePropertyChange("downloaded", oldValue, model.getDownloaded());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("sleep".equals(evt.getPropertyName())) {
            setSleep((Integer) evt.getNewValue());
        }
    }

    /**
     * Setter for property 'speed'.
     *
     * @param speed Value to set for property 'speed'.
     */
    public void setSpeed(long speed) {
        long oldValue = model.getSpeed();
        model.setSpeed(speed);
        firePropertyChange("speed", oldValue, model.getSpeed());
    }

    /**
     * Setter for property 'sleep'.
     *
     * @param sleep Value to set for property 'sleep'.
     */
    public void setSleep(int sleep) {
        int oldValue = model.getSleep();
        model.setSleep(sleep);
        firePropertyChange("sleep", oldValue, model.getSleep());
    }

    /**
     * Setter for property 'averageSpeed'.
     *
     * @param averageSpeed Value to set for property 'averageSpeed'.
     */
    public void setAverageSpeed(float averageSpeed) {
        float oldValue = model.getAverageSpeed();
        model.setAverageSpeed(averageSpeed);
        firePropertyChange("averageSpeed", oldValue, model.getAverageSpeed());
    }

    /**
     * {@inheritDoc}
     */
    public void setDescription(String description) {
        String oldValue = model.getDescription();
        model.setDescription(description);
        firePropertyChange("description", oldValue, model.getDescription());
    }

    /**
     * Setter for property 'timeToQueued'.
     *
     * @param i Value to set for property 'timeToQueued'.
     */
    public void setTimeToQueued(int i) {
        int oldValue = model.getTimeToQueued();
        model.setTimeToQueued(i);
        firePropertyChange("timeToQueued", oldValue, i);
    }

    /**
     * Setter for property 'connectionSettings'.
     *
     * @param connectionSettings Value to set for property 'connectionSettings'.
     */
    public void setConnectionSettings(final ConnectionSettings connectionSettings) {
        ConnectionSettings oldValue = model.getConnectionSettings();
        model.setConnectionSettings(connectionSettings);
        firePropertyChange("connectionSettings", oldValue, model.getConnectionSettings());
    }

    /**
     * {@inheritDoc}
     */
    public void setFileState(FileState fileState) {
        FileState oldValue = model.getFileState();
        model.setFileState(fileState);
        firePropertyChange("fileState", oldValue, model.getFileState());

    }

    public void setShortTimeAvgSpeed(final float shortTimeAvgSpeed) {
        float oldValue = model.getShortTimeAvgSpeed();
        model.setShortTimeAvgSpeed(shortTimeAvgSpeed);
        firePropertyChange("shortTimeAvgSpeed", oldValue, model.getShortTimeAvgSpeed());
    }


    public void setSpeedLimit(final int speedLimit) {
        final int oldValue = model.getSpeedLimit();
        model.setSpeedLimit(speedLimit);
        firePropertyChange("speedLimit", oldValue, model.getSpeedLimit());
    }

    public void setResumeSupported(boolean resumeSupported) {
        final boolean oldValue = model.isResumeSupported();
        model.setResumeSupported(resumeSupported);
        firePropertyChange("resumeSupported", oldValue, model.isResumeSupported());
    }

    public void setListOrder(int listOrder) {
        final int oldValue = model.getListOrder();
        model.setListOrder(listOrder);
        firePropertyChange("listOrder", oldValue, model.getListOrder());
    }

    @Override
    public String getLocalPluginConfig() {
        return model.getLocalPluginConfig();
    }

    @Override
    public void setLocalPluginConfig(String localPluginConfig) {
        final String oldValue = model.getLocalPluginConfig();
        model.setLocalPluginConfig(localPluginConfig);
        firePropertyChange("localPluginConfig", oldValue, model.getLocalPluginConfig());
    }

    public void setFileNameRenameTo(String fileNameRenameTo) {
        final String oldValue = model.getFileNameRenameTo();
        model.setFileNameRenameTo(fileNameRenameTo);
        firePropertyChange("fileNameRenameTo", oldValue, model.getFileNameRenameTo());
    }

    public void setFileUrl(URL fileUrl) {
        final URL oldValue = model.getFileUrl();
        model.setFileUrl(fileUrl);
        firePropertyChange("fileUrl", oldValue, model.getFileUrl());
    }

    public String getFileNameRenameTo() {
        return model.getFileNameRenameTo();
    }

    public void setTokens(int tokens) {
        model.setTokens(tokens);
    }

    public int getTokens() {
        return model.getTokens();
    }

    public FileInfo toFileInfo() {
        return model.toFileInfo();
    }

    public float getAverageSpeed() {
        return model.getAverageSpeed();
    }

    public String getFileType() {
        return model.getFileType();
    }

    public void setFileType(String fileType) {
        model.setFileType(fileType);
    }

    public int getTokensLimit() {
        return model.getTokensLimit();
    }

    public void setErrorAttemptsCount(int errorAttemptsCount) {
        model.setErrorAttemptsCount(errorAttemptsCount);
    }

    public void setServiceName(String serviceName) {
        model.setServiceName(serviceName);
    }

    public void setTokensLimit(int tokens) {
        model.setTokensLimit(tokens);
    }

    public void resetErrorAttempts() {
        model.resetErrorAttempts();
    }

    public void setCompleteTaskDuration(long completeTaskDuration) {
        model.setCompleteTaskDuration(completeTaskDuration);
    }

    public long getSpeed() {
        return model.getSpeed();
    }

    public float getShortTimeAvgSpeed() {
        return model.getShortTimeAvgSpeed();
    }

    public void setProperties(Map<String, Object> properties) {
        model.setProperties(properties);
    }

    public ConnectionSettings getConnectionSettings() {
        return model.getConnectionSettings();
    }

    public int getSpeedLimit() {
        return model.getSpeedLimit();
    }

    public void resetSpeed() {
        model.resetSpeed();
    }

    public void setRealDownload(long realDownload) {
        model.setRealDownload(realDownload);
    }

    public String getServiceName() {
        return model.getServiceName();
    }

    @Deprecated
    public String getShareDownloadServiceID() {
        return model.getShareDownloadServiceID();
    }

    @Deprecated
    public void setShareDownloadServiceID(String shareDownloadServiceID) {
        model.setShareDownloadServiceID(shareDownloadServiceID);
    }

    public void setTakenTokens(int takenTokens) {
        model.setTakenTokens(takenTokens);
    }

    public File getOutputFile() {
        return model.getOutputFile();
    }

    public void setSaveToDirectory(File saveToDirectory) {
        model.setSaveToDirectory(saveToDirectory);
    }

    public void setDateInserted(Date dateInserted) {
        model.setDateInserted(dateInserted);
    }

    public DownloadTask getTask() {
        return model.getTask();
    }

    public String getErrorMessage() {
        return model.getErrorMessage();
    }

    public boolean hasSpeedLimit() {
        return model.hasSpeedLimit();
    }

    public int getSleep() {
        return model.getSleep();
    }

    public void setTimeToQueuedMax(int timeToQueuedMax) {
        model.setTimeToQueuedMax(timeToQueuedMax);
    }

    public int getTimeToQueued() {
        return model.getTimeToQueued();
    }

    public long getCompleteTaskDuration() {
        return model.getCompleteTaskDuration();
    }

    public int getTakenTokens() {
        return model.getTakenTokens();
    }

    public Date getDateInserted() {
        return model.getDateInserted();
    }

    public int getTimeToQueuedMax() {
        return model.getTimeToQueuedMax();
    }

    public void setErrorMessage(String errorMessage) {
        model.setErrorMessage(errorMessage);
    }

    public int getErrorAttemptsCount() {
        return model.getErrorAttemptsCount();
    }

    public int getListOrder() {
        return model.getListOrder();
    }

    @Override
    public File getSaveToDirectory() {
        return model.getSaveToDirectory();
    }

    public DownloadState getState() {
        return model.getState();
    }

    public FileState getFileState() {
        return model.getFileState();
    }

    public URL getFileUrl() {
        return model.getFileUrl();
    }

    public void setNewURL(URL fileUrl) {
        model.setNewURL(fileUrl);
    }

    public String getFileName() {
        return model.getFileName();
    }

    @Override
    public File getStoreFile() {
        return model.getStoreFile();
    }

    public long getDownloaded() {
        return model.getDownloaded();
    }

    public String getDescription() {
        return model.getDescription();
    }

    @Override
    public void setStoreFile(File storeFile) {
        model.setStoreFile(storeFile);
    }

    public long getFileSize() {
        return model.getFileSize();
    }

    public void setPluginID(String pluginID) {
        model.setPluginID(pluginID);
    }

    public Map<String, Object> getProperties() {
        return model.getProperties();
    }

    public long getRealDownload() {
        return model.getRealDownload();
    }

    public String getPluginID() {
        return model.getPluginID();
    }

    public boolean isResumeSupported() {
        return model.isResumeSupported();
    }

    @Override
    public String toString() {
        return model.toString();
    }

}
