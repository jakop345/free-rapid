package cz.vity.freerapid.plugins.services.one23wzwp;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class One23WzwpServiceImpl extends AbstractFileShareService {

    @Override
    public String getName() {
        return "123wzwp.com";
    }

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new One23WzwpFileRunner();
    }

}