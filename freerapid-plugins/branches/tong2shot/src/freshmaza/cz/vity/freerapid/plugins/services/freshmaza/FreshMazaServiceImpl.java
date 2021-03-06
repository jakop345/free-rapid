package cz.vity.freerapid.plugins.services.freshmaza;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class FreshMazaServiceImpl extends AbstractFileShareService {

    @Override
    public String getName() {
        return "freshmaza.com";
    }

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new FreshMazaFileRunner();
    }

}