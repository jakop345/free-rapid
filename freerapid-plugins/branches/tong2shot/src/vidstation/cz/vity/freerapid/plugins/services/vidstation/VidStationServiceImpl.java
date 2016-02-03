package cz.vity.freerapid.plugins.services.vidstation;

import cz.vity.freerapid.plugins.services.xfileplayer.XFilePlayerServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class VidStationServiceImpl extends XFilePlayerServiceImpl {

    @Override
    public String getServiceTitle() {
        return "VidStation";
    }

    @Override
    public String getName() {
        return "vidstation.net";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new VidStationFileRunner();
    }

}