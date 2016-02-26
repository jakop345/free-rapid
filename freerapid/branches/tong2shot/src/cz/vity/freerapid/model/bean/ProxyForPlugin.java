package cz.vity.freerapid.model.bean;

import cz.vity.freerapid.gui.managers.interfaces.Identifiable;
import cz.vity.freerapid.gui.managers.interfaces.ModelWrapper;
import cz.vity.freerapid.model.ProxyForPluginModel;
import org.jdesktop.application.AbstractBean;

/**
 * @author tong2shot
 */
public class ProxyForPlugin extends AbstractBean implements ModelWrapper {

    private ProxyForPluginModel model;

    public ProxyForPlugin(ProxyForPluginModel model) {
        this.model = model;
    }

    @Override
    public Identifiable getModel() {
        return model;
    }

    public String getPluginId() {
        return model.getPluginId();
    }

    public void setPluginId(String pluginId) {
        String oldValue = model.getPluginId();
        model.setPluginId(pluginId);
        firePropertyChange("pluginId", oldValue, model.getPluginId());
    }

    public Long getProxySetId() {
        return model.getProxySetId();
    }

    public void setProxySetId(Long proxySetId) {
        Long oldValue = model.getProxySetId();
        model.setProxySetId(proxySetId);
        firePropertyChange("proxySetId", oldValue, model.getProxySetId());
    }

    public String getPluginServices() {
        return model.getPluginServices();
    }

    public void setPluginServices(String pluginServices) {
        String oldValue = model.getPluginServices();
        model.setPluginServices(pluginServices);
        firePropertyChange("pluginServices", oldValue, model.getPluginServices());
    }

    public String getProxySetName() {
        return model.getProxySetName();
    }

    public void setProxySetName(String proxySetName) {
        String oldValue = model.getProxySetName();
        model.setProxySetName(proxySetName);
        firePropertyChange("proxySetName", oldValue, model.getProxySetName());
    }

    @Override
    public String toString() {
        return model.toString();
    }
}
