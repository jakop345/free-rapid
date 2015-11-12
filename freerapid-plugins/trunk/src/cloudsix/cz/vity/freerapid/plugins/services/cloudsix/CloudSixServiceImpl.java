package cz.vity.freerapid.plugins.services.cloudsix;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class CloudSixServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "CloudSix";
    }

    @Override
    public String getName() {
        return "cloudsix.me";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new CloudSixFileRunner();
    }

}