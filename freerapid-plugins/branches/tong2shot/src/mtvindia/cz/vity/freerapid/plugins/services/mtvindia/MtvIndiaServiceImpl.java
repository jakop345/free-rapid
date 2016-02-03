package cz.vity.freerapid.plugins.services.mtvindia;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class MtvIndiaServiceImpl extends AbstractFileShareService {

    @Override
    public String getName() {
        return "mtvindia.com";
    }

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new MtvIndiaFileRunner();
    }

}