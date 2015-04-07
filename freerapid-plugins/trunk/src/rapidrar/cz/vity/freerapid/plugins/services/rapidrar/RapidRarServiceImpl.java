package cz.vity.freerapid.plugins.services.rapidrar;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class RapidRarServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "RapidRar";
    }

    @Override
    public String getName() {
        return "rapidrar.com";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new RapidRarFileRunner();
    }

}