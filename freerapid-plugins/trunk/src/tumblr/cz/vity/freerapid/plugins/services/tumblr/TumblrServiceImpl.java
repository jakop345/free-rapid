package cz.vity.freerapid.plugins.services.tumblr;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class TumblrServiceImpl extends AbstractFileShareService {

    @Override
    public String getName() {
        return "tumblr.com";
    }

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new TumblrFileRunner();
    }

}