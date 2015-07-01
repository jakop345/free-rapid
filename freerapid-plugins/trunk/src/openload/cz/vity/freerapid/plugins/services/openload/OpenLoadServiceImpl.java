package cz.vity.freerapid.plugins.services.openload;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class OpenLoadServiceImpl extends AbstractFileShareService {

    @Override
    public String getName() {
        return "openload.io";
    }

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new OpenLoadFileRunner();
    }

}