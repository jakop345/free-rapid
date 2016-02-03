package cz.vity.freerapid.plugins.services.mediafree;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class MediaFreeServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "MediaFree";
    }

    @Override
    public String getName() {
        return "mediafree.co";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new MediaFreeFileRunner();
    }

}