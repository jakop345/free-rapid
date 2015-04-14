package cz.vity.freerapid.plugins.services.heavyr;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class HeavyRServiceImpl extends AbstractFileShareService {

    @Override
    public String getName() {
        return "heavy-r.com";
    }

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new HeavyRFileRunner();
    }

}