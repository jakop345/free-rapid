package cz.vity.freerapid.plugins.services.excellentvideos;

import cz.vity.freerapid.plugins.services.xfileplayer.XFilePlayerServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class ExcellentVideosServiceImpl extends XFilePlayerServiceImpl {

    @Override
    public String getServiceTitle() {
        return "ExcellentVideos";
    }

    @Override
    public String getName() {
        return "excellentvideos.net";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new ExcellentVideosFileRunner();
    }

}