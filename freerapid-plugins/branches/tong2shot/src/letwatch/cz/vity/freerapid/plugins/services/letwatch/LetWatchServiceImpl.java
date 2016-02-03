package cz.vity.freerapid.plugins.services.letwatch;

import cz.vity.freerapid.plugins.services.xfileplayer.XFilePlayerServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class LetWatchServiceImpl extends XFilePlayerServiceImpl {

    @Override
    public String getServiceTitle() {
        return "LetWatch";
    }

    @Override
    public String getName() {
        return "letwatch.us";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new LetWatchFileRunner();
    }

}