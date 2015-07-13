package cz.vity.freerapid.plugins.services.megacache;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class MegaCacheServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "MegaCache";
    }

    @Override
    public String getName() {
        return "megacache.net";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new MegaCacheFileRunner();
    }

}