package cz.vity.freerapid.plugins.services.onepan;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class OnePanServiceImpl extends AbstractFileShareService {

    @Override
    public String getName() {
        return "1pan.cc";
    }

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new OnePanFileRunner();
    }

}