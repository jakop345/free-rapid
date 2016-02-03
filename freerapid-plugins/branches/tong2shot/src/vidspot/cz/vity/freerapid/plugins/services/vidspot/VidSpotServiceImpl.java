package cz.vity.freerapid.plugins.services.vidspot;

import cz.vity.freerapid.plugins.services.xfileplayer.XFilePlayerServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class VidSpotServiceImpl extends XFilePlayerServiceImpl {

    @Override
    public String getServiceTitle() {
        return "VidSpot";
    }

    @Override
    public String getName() {
        return "vidspot.net";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new VidSpotFileRunner();
    }

}