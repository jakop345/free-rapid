package cz.vity.freerapid.plugins.services.rocvideo;

import cz.vity.freerapid.plugins.services.xfileplayer.XFilePlayerServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class RocVideoServiceImpl extends XFilePlayerServiceImpl {

    @Override
    public String getServiceTitle() {
        return "RocVideo";
    }

    @Override
    public String getName() {
        return "rocvideo.tv";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new RocVideoFileRunner();
    }

}