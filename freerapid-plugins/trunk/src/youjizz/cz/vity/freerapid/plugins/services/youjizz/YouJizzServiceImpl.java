package cz.vity.freerapid.plugins.services.youjizz;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class YouJizzServiceImpl extends AbstractFileShareService {

    @Override
    public String getName() {
        return "youjizz.com";
    }

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new YouJizzFileRunner();
    }

}