package cz.vity.freerapid.plugins.services.wavymotion;

import cz.vity.freerapid.plugins.services.xfileplayer.XFilePlayerServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class WavyMotionServiceImpl extends XFilePlayerServiceImpl {

    @Override
    public String getServiceTitle() {
        return "WavyMotion";
    }

    @Override
    public String getName() {
        return "wavymotion.com";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new WavyMotionFileRunner();
    }

}