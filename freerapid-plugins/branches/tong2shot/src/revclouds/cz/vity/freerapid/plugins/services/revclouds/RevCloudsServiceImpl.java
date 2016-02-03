package cz.vity.freerapid.plugins.services.revclouds;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class RevCloudsServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "RevClouds";
    }

    @Override
    public String getName() {
        return "revclouds.com";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new RevCloudsFileRunner();
    }

}