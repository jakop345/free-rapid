package cz.vity.freerapid.model.bean;

import com.sleepycat.persist.model.Entity;
import cz.vity.freerapid.model.PluginMetaDataModel;
import org.java.plugin.registry.PluginDescriptor;
import org.jdesktop.application.AbstractBean;

import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
@Entity
final public class PluginMetaData extends AbstractBean implements Comparable<PluginMetaData> {
    private final static Logger logger = Logger.getLogger(PluginMetaData.class.getName());

    private PluginMetaDataModel model;

    public PluginMetaData(PluginMetaDataModel model) {
        this.model = model;
    }

    public PluginMetaDataModel getModel() {
        return model;
    }

    public void setEnabled(boolean enabled) {
        boolean oldValue = model.isEnabled();
        model.setEnabled(enabled);
        firePropertyChange("enabled", oldValue, model.isEnabled());
    }

    public void setUpdatesEnabled(boolean updatesEnabled) {
        boolean oldValue = model.isUpdatesEnabled();
        model.setUpdatesEnabled(updatesEnabled);
        firePropertyChange("updatesEnabled", oldValue, model.isUpdatesEnabled());
    }

    public void setPluginPriority(int value) {
        Integer oldValue = model.getPluginPriority();
        model.setPluginPriority(value);
        firePropertyChange("priority", oldValue, model.getPluginPriority());
    }

    public void setMaxAllowedDownloads(int maxAllowedDownloads) {
        int oldValue = model.getMaxAllowedDownloads();
        model.setMaxAllowedDownloads(maxAllowedDownloads);
        firePropertyChange("maxAllowedDownloads", oldValue, model.getMaxAllowedDownloads());
    }

    public void setClipboardMonitored(boolean clipboardMonitored) {
        final boolean oldValue = model.isClipboardMonitored();
        model.setClipboardMonitored(clipboardMonitored);
        firePropertyChange("clipboardMonitored", oldValue, model.isClipboardMonitored());
    }

    public void setPluginDescriptor(PluginDescriptor descriptor) {
        model.setPluginDescriptor(descriptor);
    }

    public boolean isSupported(String url) {
        return model.isSupported(url);
    }

    public boolean isDescriptorSet() {
        return model.isDescriptorSet();
    }

    public boolean isLibraryPlugin() {
        return model.isLibraryPlugin();
    }

    public boolean isPremium() {
        return model.isPremium();
    }

    public boolean isUpdatesEnabled() {
        return model.isUpdatesEnabled();
    }

    public String getVendor() {
        return model.getVendor();
    }

    public String getId() {
        return model.getId();
    }

    public void setId(String id) {
        model.setId(id);
    }

    public boolean isResumeSupported() {
        return model.isResumeSupported();
    }

    public int getMaxAllowedDownloads() {
        return model.getMaxAllowedDownloads();
    }

    public boolean isEnabled() {
        return model.isEnabled();
    }

    public boolean isOptionable() {
        return model.isOptionable();
    }

    @Override
    public int compareTo(PluginMetaData o) {
        return model.getId().compareToIgnoreCase(o.model.getId());
    }

    public void setResumeSupported(boolean resumeSupported) {
        model.setResumeSupported(resumeSupported);
    }

    public String getServices() {
        return model.getServices();
    }

    public String getWWW() {
        return model.getWWW();
    }

    public int getMaxParallelDownloads() {
        return model.getMaxParallelDownloads();
    }

    public void setMaxParallelDownloads(int maxParallelDownloads) {
        model.setMaxParallelDownloads(maxParallelDownloads);
    }

    public boolean isRemoveCompleted() {
        return model.isRemoveCompleted();
    }

    public int getPluginPriority() {
        return model.getPluginPriority();
    }

    public String getVersion() {
        return model.getVersion();
    }

    public void setRemoveCompleted(boolean removeCompleted) {
        model.setRemoveCompleted(removeCompleted);
    }

    public boolean hasFavicon() {
        return model.hasFavicon();
    }

    public boolean isClipboardMonitored() {
        return model.isClipboardMonitored();
    }

    @Deprecated
    public void setPriority(int value) {
        model.setPriority(value);
    }

    public static Collection<PluginMetaDataModel> toModels(Collection<PluginMetaData> dataCollection) {
        Collection<PluginMetaDataModel> results = new LinkedList<>();
        for (PluginMetaData data : dataCollection) {
            results.add(data.getModel());
        }
        return results;
    }
}
