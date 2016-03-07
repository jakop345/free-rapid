package cz.vity.freerapid.plugins.services.ouo;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class OuoServiceImpl extends AbstractFileShareService {

    @Override
    public String getName() {
        return "ouo.io";
    }

    @Override
    public boolean supportsRunCheck() {
        return false;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new OuoFileRunner();
    }

}