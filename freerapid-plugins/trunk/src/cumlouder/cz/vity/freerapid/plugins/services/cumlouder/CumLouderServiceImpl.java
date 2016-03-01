package cz.vity.freerapid.plugins.services.cumlouder;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class CumLouderServiceImpl extends AbstractFileShareService {

    @Override
    public String getName() {
        return "cumlouder.com";
    }

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new CumLouderFileRunner();
    }

}