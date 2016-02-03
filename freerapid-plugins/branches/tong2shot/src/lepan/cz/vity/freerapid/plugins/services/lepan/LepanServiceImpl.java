package cz.vity.freerapid.plugins.services.lepan;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class LepanServiceImpl extends AbstractFileShareService {

    @Override
    public String getName() {
        return "lepan.cc";
    }

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new LepanFileRunner();
    }

}