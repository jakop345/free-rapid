package cz.vity.freerapid.plugins.services.roottail;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class RootTailServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "RootTail";
    }

    @Override
    public String getName() {
        return "roottail.com";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new RootTailFileRunner();
    }

}