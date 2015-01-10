package cz.vity.freerapid.plugins.services.anime4you;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class Anime4YouServiceImpl extends AbstractFileShareService {

    @Override
    public String getName() {
        return "anime4you.net";
    }

    @Override
    public boolean supportsRunCheck() {
        return false;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new Anime4YouFileRunner();
    }

}