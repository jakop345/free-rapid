package cz.vity.freerapid.plugins.services.fastpic;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class FastPicServiceImpl extends AbstractFileShareService {

    @Override
    public String getName() {
        return "fastpic.ru";
    }

    @Override
    public boolean supportsRunCheck() {
        return false;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new FastPicFileRunner();
    }

}