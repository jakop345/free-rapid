package cz.vity.freerapid.model.bean;

import cz.vity.freerapid.gui.managers.interfaces.ModelWrapper;
import cz.vity.freerapid.model.ProxySetModel;
import org.jdesktop.application.AbstractBean;

import java.util.List;

/**
 * @author tong2shot
 */
public class ProxySet extends AbstractBean implements ModelWrapper {

    private ProxySetModel model;

    public ProxySet(ProxySetModel model) {
        this.model = model;
    }

    @Override
    public ProxySetModel getModel() {
        return model;
    }

    public String getName() {
        return model.getName();
    }

    public void setName(String name) {
        String oldValue = model.getName();
        model.setName(name);
        firePropertyChange("name", oldValue, model.getName());
    }

    public List<String> getProxies() {
        return model.getProxies();
    }

    public void setProxies(List<String> proxies) {
        List<String> oldValue = model.getProxies();
        model.setProxies(proxies);
        firePropertyChange("proxies", oldValue, model.getProxies());
    }

    public Long getIdentificator() {
        return model.getIdentificator();
    }

    @Override
    public String toString() {
        return model.getName();
    }
}
