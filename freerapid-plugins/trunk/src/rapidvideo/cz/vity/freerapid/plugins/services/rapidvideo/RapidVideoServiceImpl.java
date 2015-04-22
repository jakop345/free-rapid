package cz.vity.freerapid.plugins.services.rapidvideo;

import cz.vity.freerapid.plugins.services.xfileplayer.XFilePlayerServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class RapidVideoServiceImpl extends XFilePlayerServiceImpl{

    @Override
    public String getServiceTitle() {
        return "RapidVideo";
    }

    @Override
    public String getName() {
        return "rapidvideo.ws";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new RapidVideoFileRunner();
    }

}