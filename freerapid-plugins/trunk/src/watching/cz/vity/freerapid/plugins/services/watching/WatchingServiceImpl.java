package cz.vity.freerapid.plugins.services.watching;

import cz.vity.freerapid.plugins.services.xfileplayer.XFilePlayerServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class WatchingServiceImpl extends XFilePlayerServiceImpl {

    @Override
    public String getServiceTitle() {
        return "Watching";
    }

    @Override
    public String getName() {
        return "watching.to";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new WatchingFileRunner();
    }

}