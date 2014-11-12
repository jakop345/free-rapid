package cz.vity.freerapid.plugins.services.videoserver;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class VideoServerServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "VideoServer";
    }

    @Override
    public String getName() {
        return "videoserver.biz";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new VideoServerFileRunner();
    }

}