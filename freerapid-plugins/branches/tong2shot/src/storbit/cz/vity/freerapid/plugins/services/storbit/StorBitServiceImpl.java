package cz.vity.freerapid.plugins.services.storbit;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class StorBitServiceImpl extends AbstractFileShareService {

    @Override
    public String getName() {
        return "storbit.net";
    }

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new StorBitFileRunner();
    }

}