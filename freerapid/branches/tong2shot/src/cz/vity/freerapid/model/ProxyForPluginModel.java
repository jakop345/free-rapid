package cz.vity.freerapid.model;

import com.sleepycat.persist.model.*;
import cz.vity.freerapid.gui.managers.interfaces.Identifiable;
import cz.vity.freerapid.gui.managers.interfaces.ModelWrapper;
import cz.vity.freerapid.model.bean.ProxyForPlugin;

/**
 * @author tong2shot
 */
@Entity(version = 1)
public class ProxyForPluginModel implements Identifiable {
    @PrimaryKey(sequence = "ID")
    private Long dbId;

    private String pluginId;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE,
            relatedEntity = ProxySetModel.class,
            onRelatedEntityDelete = DeleteAction.CASCADE)
    private Long proxySetId;

    private boolean enabled = true;

    @NotPersistent
    private String pluginServices;
    @NotPersistent
    private String proxySetName;

    private ProxyForPluginModel() {
        //
    }

    public ProxyForPluginModel(String pluginId, Long proxySetId) {
        this.pluginId = pluginId;
        this.proxySetId = proxySetId;
    }

    public String getPluginId() {
        return pluginId;
    }

    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    public Long getProxySetId() {
        return proxySetId;
    }

    public void setProxySetId(Long proxySetId) {
        this.proxySetId = proxySetId;
    }

    public String getPluginServices() {
        return pluginServices;
    }

    public void setPluginServices(String pluginServices) {
        this.pluginServices = pluginServices;
    }

    public String getProxySetName() {
        return proxySetName;
    }

    public void setProxySetName(String proxySetName) {
        this.proxySetName = proxySetName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Long getIdentificator() {
        return dbId;
    }

    @Override
    public ModelWrapper build() {
        return new ProxyForPlugin(this);
    }

}
