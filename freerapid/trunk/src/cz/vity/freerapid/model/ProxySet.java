package cz.vity.freerapid.model;

import cz.vity.freerapid.gui.managers.interfaces.Identifiable;
import org.jdesktop.application.AbstractBean;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tong2shot
 */
@Entity
public class ProxySet extends AbstractBean implements Identifiable {

    @Id
    @GeneratedValue
    private Long dbId;

    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> proxies = new ArrayList<String>();

    private ProxySet() {
        //
    }

    public ProxySet(String name, List<String> proxies) {
        this.name = name;
        this.proxies = proxies;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        String oldValue = this.name;
        this.name = name;
        firePropertyChange("name", oldValue, this.name);
    }

    public List<String> getProxies() {
        return proxies;
    }

    public void setProxies(List<String> proxies) {
        List<String> oldValue = this.proxies;
        this.proxies = proxies;
        firePropertyChange("proxies", oldValue, this.proxies);
    }

    @Override
    public Long getIdentificator() {
        return dbId;
    }

    @Override
    public String toString() {
        return name;
    }

}
