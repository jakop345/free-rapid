package cz.vity.freerapid.model;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.NotPersistent;
import com.sleepycat.persist.model.PrimaryKey;
import cz.vity.freerapid.core.Consts;
import cz.vity.freerapid.gui.managers.interfaces.Identifiable;
import cz.vity.freerapid.model.bean.PluginMetaData;
import cz.vity.freerapid.utilities.DescriptorUtils;
import org.java.plugin.registry.PluginDescriptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * @author Ladislav Vitasek
 */
@Entity
final public class PluginMetaDataModel implements Identifiable, Comparable<PluginMetaDataModel> {
    private final static Logger logger = Logger.getLogger(PluginMetaDataModel.class.getName());
    private final static Pattern NOPE_URL_MATCHER = Pattern.compile("&&&XXX&&&");

    @PrimaryKey(sequence = "ID")
    private Long dbId;

    //persisted info
    private String id;
    private boolean updatesEnabled;
    private boolean enabled;
    private int pluginPriority;
    private int maxAllowedDownloads;
    private boolean clipboardMonitored;
    private boolean removeCompleted;

    @NotPersistent
    private Pattern supportedURL;
    @NotPersistent
    private PluginDescriptor descriptor;
    @NotPersistent
    private boolean hasOptions;
    @NotPersistent
    private String services;
    @NotPersistent
    private String www;
    @NotPersistent
    private boolean premium;
    @NotPersistent
    private boolean favicon;
    @NotPersistent
    private boolean resumeSupported;
    @NotPersistent
    private int maxParallelDownloads;
    @NotPersistent
    private boolean libraryPlugin;

    public PluginMetaDataModel() {
        //default values
        this.enabled = true;
        this.clipboardMonitored = true;
        this.updatesEnabled = true;
        this.pluginPriority = -1;
        this.maxAllowedDownloads = -1;
        this.libraryPlugin = false;
    }

    public PluginMetaDataModel(PluginDescriptor descriptor) {
        this();
        this.descriptor = descriptor;
        this.id = descriptor.getId();
        setPluginDescriptor(descriptor);
    }

    public void setPluginDescriptor(PluginDescriptor descriptor) {
        this.descriptor = descriptor;
        hasOptions = DescriptorUtils.getAttribute("hasOptions", false, descriptor);
        services = DescriptorUtils.getAttribute("services", getId(), descriptor).toLowerCase(Locale.ENGLISH);
        www = DescriptorUtils.getAttribute("www", Consts.WEBURL, descriptor);
        premium = DescriptorUtils.getAttribute("premium", false, descriptor);
        favicon = DescriptorUtils.getAttribute("faviconImage", null, descriptor) != null;
        setRemoveCompleted(DescriptorUtils.getAttribute("removeCompleted", false, descriptor));
        setMaxParallelDownloads(DescriptorUtils.getAttribute("maxDownloads", 1, descriptor));
        libraryPlugin = DescriptorUtils.getAttribute("libraryPlugin", false, descriptor) || maxParallelDownloads == 0;
        if (pluginPriority == -1)
            pluginPriority = DescriptorUtils.getAttribute("priority", (premium) ? 1000 : 100, descriptor);
        if (libraryPlugin) {
            supportedURL = NOPE_URL_MATCHER;
        } else {
            supportedURL = Pattern.compile(DescriptorUtils.getAttribute("urlRegex", "&&&XX&&&", descriptor), Pattern.CASE_INSENSITIVE);
        }
        if (maxAllowedDownloads > 1) {
            setMaxAllowedDownloads(Math.min(maxParallelDownloads, maxAllowedDownloads));
        } else {
            if (maxAllowedDownloads == -1) setMaxAllowedDownloads(Math.max(1, Math.min(DescriptorUtils.getAttribute("defaultDownloads", maxParallelDownloads, descriptor), maxParallelDownloads)));
        }

        resumeSupported = DescriptorUtils.getAttribute("resumeSupported", true, descriptor);
    }

    public boolean isLibraryPlugin() {
        return libraryPlugin;
    }

    /**
     * Overuje, zda je dane URL podporovane mezi pluginy
     *
     * @param url
     * @return vraci v pripade, ze nejaky plugin podporuje dane URL, jinak false
     */
    public boolean isSupported(final String url) {
        return !isLibraryPlugin() && supportedURL.matcher(url).matches();
    }


    public void setRemoveCompleted(boolean removeCompleted) {
        this.removeCompleted = removeCompleted;
    }

    public void setResumeSupported(boolean resumeSupported) {
        this.resumeSupported = resumeSupported;
    }

    public void setMaxParallelDownloads(int maxParallelDownloads) {
        this.maxParallelDownloads = maxParallelDownloads;
    }

    public Long getIdentificator() {
        return dbId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isOptionable() {
        return hasOptions;
    }

    public String getServices() {
        return services;
    }

    public String getVendor() {
        return descriptor.getVendor();
    }

    public String getWWW() {
        return www;
    }

    public boolean isDescriptorSet() {
        return descriptor != null;
    }


    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isUpdatesEnabled() {
        return updatesEnabled;
    }

    public void setUpdatesEnabled(boolean updatesEnabled) {
        this.updatesEnabled = updatesEnabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PluginMetaDataModel that = (PluginMetaDataModel) o;

        return id.equals(that.id);

    }

    @Override
    public int compareTo(PluginMetaDataModel o) {
        return this.id.compareToIgnoreCase(o.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }


    public String getVersion() {
        return descriptor.getVersion().toString();
    }

    public boolean isPremium() {
        return premium;
    }

    public boolean hasFavicon() {
        return favicon;
    }

    public boolean isRemoveCompleted() {
        return removeCompleted;
    }

    public String toString() {
        return "PluginMetaData{" +
                "dbId=" + dbId +
                " id='" + id + '\'' + " Version=" + getVersion() +
                '}';
    }

    public boolean isResumeSupported() {
        return resumeSupported;
    }


    public int getMaxParallelDownloads() {
        return maxParallelDownloads;
    }

    public int getPluginPriority() {
        return pluginPriority;
    }

    @Deprecated
    /**
     * because of XMLserialization in 0.85a3 - to 0.85a4
     */
    public void setPriority(int value) {
        //this.setPluginPriority(value);   
    }


    public int getMaxAllowedDownloads() {
        return maxAllowedDownloads;
    }

    public boolean isClipboardMonitored() {
        return clipboardMonitored;
    }

    public void setPluginPriority(int value) {
        this.pluginPriority = value;
    }

    public void setMaxAllowedDownloads(int maxAllowedDownloads) {
        this.maxAllowedDownloads = maxAllowedDownloads;
    }

    public void setClipboardMonitored(boolean clipboardMonitored) {
        this.clipboardMonitored = clipboardMonitored;
    }

    public static Collection<PluginMetaData> toBeans(Collection<PluginMetaDataModel> models) {
        Collection<PluginMetaData> results = new ArrayList<PluginMetaData>();
        for (PluginMetaDataModel model : models) {
            results.add(new PluginMetaData(model));
        }
        return results;
    }


}
