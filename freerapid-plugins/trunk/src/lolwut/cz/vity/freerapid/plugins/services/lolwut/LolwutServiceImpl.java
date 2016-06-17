package cz.vity.freerapid.plugins.services.lolwut;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class LolwutServiceImpl extends AbstractFileShareService {

    @Override
    public String getName() {
        return "lolwut.club";
    }

    @Override
    public boolean supportsRunCheck() {
        return false;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new LolwutFileRunner();
    }

}