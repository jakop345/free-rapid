package cz.vity.freerapid.plugins.services.chronos;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class ChronosServiceImpl extends AbstractFileShareService {

    @Override
    public String getName() {
        return "chronos.to";
    }

    @Override
    public boolean supportsRunCheck() {
        return false;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new ChronosFileRunner();
    }

}