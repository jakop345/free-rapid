package cz.vity.freerapid.plugins.services.sibit;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class SiBitServiceImpl extends AbstractFileShareService {

    @Override
    public String getName() {
        return "sibit.net";
    }

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new SiBitFileRunner();
    }

}