package cz.vity.freerapid.plugins.services.vidxtreme;

import cz.vity.freerapid.plugins.services.xfileplayer.XFilePlayerServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class VidXtremeServiceImpl extends XFilePlayerServiceImpl {

    @Override
    public String getServiceTitle() {
        return "VidXtreme";
    }

    @Override
    public String getName() {
        return "vidxtreme.to";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new VidXtremeFileRunner();
    }

}