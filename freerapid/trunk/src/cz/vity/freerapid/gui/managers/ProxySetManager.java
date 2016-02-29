package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.model.ProxySet;
import org.jdesktop.application.AbstractBean;

import java.util.Collection;

/**
 * @author tong2shot
 */
public class ProxySetManager extends AbstractBean {
    private final ManagerDirector director;

    public ProxySetManager(ManagerDirector director) {
        this.director = director;
        init();
    }

    private void init() {
        //
    }

    public Collection<ProxySet> getItems() {
        return director.getDatabaseManager().loadAll(ProxySet.class);
    }

    public void addProxySetItem(final ProxySet item) {
        Runnable runnable = new Runnable() {
            public void run() {
                director.getDatabaseManager().saveOrUpdate(item);
            }
        };
        director.getDatabaseManager().runOnTask(runnable);
        fireDataAdded(item);
    }

    public void updateProxySetItem(final ProxySet item) {
        Runnable runnable = new Runnable() {
            public void run() {
                director.getDatabaseManager().saveOrUpdate(item);
            }
        };
        director.getDatabaseManager().runOnTask(runnable);
        fireDataModified();
    }

    public void clearProxySet(Runnable succeeded) {
        Runnable runnable = new Runnable() {
            public void run() {
                director.getDatabaseManager().removeAll(ProxySet.class);
            }
        };
        director.getDatabaseManager().runOnTask(runnable, succeeded);
        fireDataModified();
    }


    public void removeItems(final Collection<ProxySet> items) {
        Runnable runnable = new Runnable() {
            public void run() {
                director.getDatabaseManager().removeCollection(items);
            }
        };
        director.getDatabaseManager().runOnTask(runnable, null);
        fireDataModified();
    }

    public void removeItems(final Collection<ProxySet> items, Runnable succeeded) {
        Runnable runnable = new Runnable() {
            public void run() {
                director.getDatabaseManager().removeCollection(items);
            }
        };
        director.getDatabaseManager().runOnTask(runnable, succeeded);
        fireDataModified();
    }

    public boolean isNameExists(final String name) {
        for (ProxySet item : getItems()) {
            if (item.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public ProxySet getProxySetFromId(final long proxySetId) {
        for (ProxySet item : getItems()) {
            if (item.getIdentificator() == proxySetId) {
                return item;
            }
        }
        return null;
    }

    private void fireDataAdded(ProxySet dataAdded) {
        firePropertyChange("dataAdded", null, dataAdded);
    }

    private void fireDataModified() {
        firePropertyChange("dataModified", null, null);
    }

}
