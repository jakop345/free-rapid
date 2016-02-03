package cz.vity.freerapid.plugins.services.tudou;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author ntoskrnl
 */
public class TudouServiceImpl extends AbstractFileShareService {

    @Override
    public String getName() {
        return "tudou.com";
    }

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new TudouFileRunner();
    }

}