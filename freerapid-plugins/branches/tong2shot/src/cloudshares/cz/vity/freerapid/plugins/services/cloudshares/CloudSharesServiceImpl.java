package cz.vity.freerapid.plugins.services.cloudshares;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class CloudSharesServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "CloudShares";
    }

    @Override
    public String getName() {
        return "cloudshares.net";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new CloudSharesFileRunner();
    }

}