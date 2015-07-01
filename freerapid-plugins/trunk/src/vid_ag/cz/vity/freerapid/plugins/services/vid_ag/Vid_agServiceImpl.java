package cz.vity.freerapid.plugins.services.vid_ag;

import cz.vity.freerapid.plugins.services.xfileplayer.XFilePlayerServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class Vid_agServiceImpl extends XFilePlayerServiceImpl {

    @Override
    public String getServiceTitle() {
        return "vid.ag";
    }

    @Override
    public String getName() {
        return "vid.ag";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new Vid_agFileRunner();
    }

}