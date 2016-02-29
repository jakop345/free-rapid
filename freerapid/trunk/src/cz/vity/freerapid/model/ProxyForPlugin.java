package cz.vity.freerapid.model;

import cz.vity.freerapid.gui.managers.interfaces.Identifiable;
import org.jdesktop.application.AbstractBean;

import javax.persistence.*;

/**
 * @author tong2shot
 */
@Entity
public class ProxyForPlugin extends AbstractBean implements Identifiable {

    @Id
    @GeneratedValue
    private Long dbId;

    private String pluginId;

    @ManyToOne(optional = false, targetEntity = ProxySet.class, cascade = CascadeType.REMOVE)
    private Long proxySetId;

    private boolean enabled = true;

    @Transient
    private String pluginServices;
    @Transient
    private String proxySetName;


    private ProxyForPlugin() {
        //
    }

    public ProxyForPlugin(String pluginId, Long proxySetId) {
        this.pluginId = pluginId;
        this.proxySetId = proxySetId;
    }

    public String getPluginId() {
        return pluginId;
    }

    public void setPluginId(String pluginId) {
        String oldValue = this.pluginId;
        this.pluginId = pluginId;
        firePropertyChange("pluginId", oldValue, this.pluginId);
    }

    public Long getProxySetId() {
        return proxySetId;
    }

    public void setProxySetId(Long proxySetId) {
        Long oldValue = this.getProxySetId();
        this.proxySetId = proxySetId;
        firePropertyChange("proxySetId", oldValue, this.proxySetId);
    }

    public String getPluginServices() {
        return pluginServices;
    }

    public void setPluginServices(String pluginServices) {
        String oldValue = this.pluginServices;
        this.pluginServices = pluginServices;
        firePropertyChange("pluginServices", oldValue, this.pluginServices);
    }

    public String getProxySetName() {
        return proxySetName;
    }

    public void setProxySetName(String proxySetName) {
        String oldValue = this.proxySetName;
        this.proxySetName = proxySetName;
        firePropertyChange("proxySetName", oldValue, this.proxySetName);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        boolean oldValue = this.enabled;
        this.enabled = enabled;
        firePropertyChange("enabled", oldValue, this.enabled);
    }

    @Override
    public Long getIdentificator() {
        return dbId;
    }

}
