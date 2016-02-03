package cz.vity.freerapid.plugins.services.uploadshub;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class UploadsHubServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "UploadsHub";
    }

    @Override
    public String getName() {
        return "uploadshub.com";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new UploadsHubFileRunner();
    }

}