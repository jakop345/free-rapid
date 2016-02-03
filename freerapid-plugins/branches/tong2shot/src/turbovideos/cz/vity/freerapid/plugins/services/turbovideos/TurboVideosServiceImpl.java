package cz.vity.freerapid.plugins.services.turbovideos;

import cz.vity.freerapid.plugins.services.xfileplayer.XFilePlayerServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class TurboVideosServiceImpl extends XFilePlayerServiceImpl {

    @Override
    public String getServiceTitle() {
        return "TurboVideos";
    }

    @Override
    public String getName() {
        return "turbovideos.net";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new TurboVideosFileRunner();
    }

}