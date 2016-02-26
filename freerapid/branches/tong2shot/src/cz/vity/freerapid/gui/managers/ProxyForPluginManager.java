package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.gui.managers.exceptions.NotSupportedDownloadServiceException;
import cz.vity.freerapid.model.ProxyForPluginModel;
import cz.vity.freerapid.model.bean.PluginMetaData;
import cz.vity.freerapid.model.bean.ProxyForPlugin;
import cz.vity.freerapid.model.bean.ProxySet;
import org.jdesktop.application.AbstractBean;
import org.jdesktop.application.ApplicationContext;

import java.util.*;
import java.util.logging.Logger;

/**
 * @author tong2shot
 */
public class ProxyForPluginManager extends AbstractBean {
    private final static Logger logger = Logger.getLogger(PluginsManager.class.getName());

    private final Object lock = new Object();

    private final ApplicationContext context;
    private final ManagerDirector director;


    public ProxyForPluginManager(ApplicationContext context, ManagerDirector director) {
        this.context = context;
        this.director = director;
        init();
    }

    private void init() {
        //
    }

    public Collection<ProxyForPlugin> getItems() {
        synchronized (lock) {
            Collection<ProxyForPlugin> proxyForPlugins = director.getDatabaseManager().loadAll(ProxyForPluginModel.class);
            Collection<ProxySet> proxies = director.getProxySetManager().getItems();
            Map<Long, ProxySet> proxySetMap = new HashMap<Long, ProxySet>(proxies.size());
            for (ProxySet proxySet : proxies) {
                proxySetMap.put(proxySet.getIdentificator(), proxySet);
            }

            List<ProxyForPlugin> toRemoveList = new LinkedList<ProxyForPlugin>();
            for (ProxyForPlugin proxyForPlugin : proxyForPlugins) {
                PluginMetaData plugin = null;
                try {
                    plugin = director.getPluginsManager().getPluginMetadata(proxyForPlugin.getPluginId());
                } catch (NotSupportedDownloadServiceException e) {
                    //
                }
                ProxySet proxySet = proxySetMap.get(proxyForPlugin.getProxySetId());
                if (plugin == null || proxySet == null) {
                    toRemoveList.add(proxyForPlugin);
                } else {
                    proxyForPlugin.setPluginServices(plugin.getServices());
                    proxyForPlugin.setProxySetName(proxySet.getName());
                }
            }
            if (toRemoveList.size() > 0) {
                proxyForPlugins.removeAll(toRemoveList);
            }
            return proxyForPlugins;
        }
    }

    public List<String> getProxies(final String pluginId) {
        Collection<ProxyForPlugin> proxyForPlugins = director.getDatabaseManager().loadAll(ProxyForPluginModel.class);
        ProxySet proxySet = null;
        for (ProxyForPlugin proxyForPlugin : proxyForPlugins) {
            if (proxyForPlugin.getPluginId().equals(pluginId)) {
                proxySet = director.getProxySetManager().getProxySetFromId(proxyForPlugin.getProxySetId());
                break;
            }
        }
        if (proxySet == null) {
            return null;
        }
        return new ArrayList<String>(proxySet.getProxies());
    }

    public void addProxyForPluginItem(final ProxyForPlugin item) {
        Runnable runnable = new Runnable() {
            public void run() {
                director.getDatabaseManager().saveOrUpdate(item, ProxyForPluginModel.class);
            }
        };
        director.getDatabaseManager().runOnTask(runnable);
        fireDataAdded(item);
    }

    public void updateProxyForPluginItem(final ProxyForPlugin item) {
        Runnable runnable = new Runnable() {
            public void run() {
                director.getDatabaseManager().saveOrUpdate(item, ProxyForPluginModel.class);
            }
        };
        director.getDatabaseManager().runOnTask(runnable);
    }

    public void clearProxyForPlugin(Runnable succeeded) {
        Runnable runnable = new Runnable() {
            public void run() {
                director.getDatabaseManager().removeAll(ProxyForPluginModel.class);
            }
        };
        director.getDatabaseManager().runOnTask(runnable, succeeded);
    }


    public void removeItems(final Collection<ProxyForPlugin> items) {
        Runnable runnable = new Runnable() {
            public void run() {
                director.getDatabaseManager().removeCollection(items, ProxyForPluginModel.class);
            }
        };
        director.getDatabaseManager().runOnTask(runnable, null);
    }

    public void removeItems(final Collection<ProxyForPlugin> items, Runnable succeeded) {
        Runnable runnable = new Runnable() {
            public void run() {
                director.getDatabaseManager().removeCollection(items, ProxyForPluginModel.class);
            }
        };
        director.getDatabaseManager().runOnTask(runnable, succeeded);
    }

    public boolean isPluginExists(final String pluginId) {
        for (ProxyForPlugin item : getItems()) {
            if (item.getPluginId().equals(pluginId)) {
                return true;
            }
        }
        return false;
    }

    private void fireDataAdded(ProxyForPlugin dataAdded) {
        firePropertyChange("dataAdded", null, dataAdded);
    }

}
