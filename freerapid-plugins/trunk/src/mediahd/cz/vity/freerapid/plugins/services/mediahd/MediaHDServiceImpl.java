package cz.vity.freerapid.plugins.services.mediahd;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class MediaHDServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "MediaHD";
    }

    @Override
    public String getName() {
        return "mediahd.co";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new MediaHDFileRunner();
    }

}