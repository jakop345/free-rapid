package cz.vity.freerapid.plugins.services.vishare;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class VishareServiceImpl extends AbstractFileShareService {

    @Override
    public String getName() {
        return "vishare.us";
    }

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new VishareFileRunner();
    }

}