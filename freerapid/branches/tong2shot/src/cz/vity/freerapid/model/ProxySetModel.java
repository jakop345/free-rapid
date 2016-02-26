package cz.vity.freerapid.model;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import cz.vity.freerapid.gui.managers.interfaces.Identifiable;
import cz.vity.freerapid.gui.managers.interfaces.ModelWrapper;
import cz.vity.freerapid.model.bean.ProxySet;

import java.util.HashSet;
import java.util.Set;

/**
 * @author tong2shot
 */
@Entity
public class ProxySetModel implements Identifiable {

    @PrimaryKey(sequence = "ID")
    private Long dbId;

    private String name;
    private Set<String> proxies = new HashSet<String>();

    private ProxySetModel() {
        //
    }

    public ProxySetModel(String name, Set<String> proxies) {
        this.name = name;
        this.proxies = proxies;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getProxies() {
        return proxies;
    }

    public void setProxies(Set<String> proxies) {
        this.proxies = proxies;
    }

    @Override
    public Long getIdentificator() {
        return dbId;
    }

    @Override
    public ModelWrapper build() {
        return new ProxySet(this);
    }
}
