package cz.vity.freerapid.plugins.services.streamvids;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class StreamVidsServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "StreamVids";
    }

    @Override
    public String getName() {
        return "streamvids.net";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new StreamVidsFileRunner();
    }

}