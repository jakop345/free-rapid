package cz.vity.freerapid.plugins.services.faststore;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class FastStoreServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "FastStore";
    }

    @Override
    public String getName() {
        return "faststore.org";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new FastStoreFileRunner();
    }

}