package cz.vity.freerapid.plugins.services.bestvidsnow;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class BestVidsNowServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "BestVidsNow";
    }

    @Override
    public String getName() {
        return "bestvidsnow.com";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new BestVidsNowFileRunner();
    }

}