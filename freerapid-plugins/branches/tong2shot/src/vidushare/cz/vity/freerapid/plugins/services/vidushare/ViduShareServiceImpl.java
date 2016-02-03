package cz.vity.freerapid.plugins.services.vidushare;

import cz.vity.freerapid.plugins.services.xfileplayer.XFilePlayerServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class ViduShareServiceImpl extends XFilePlayerServiceImpl {

    @Override
    public String getServiceTitle() {
        return "ViduShare";
    }

    @Override
    public String getName() {
        return "vidushare.com";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new ViduShareFileRunner();
    }

}