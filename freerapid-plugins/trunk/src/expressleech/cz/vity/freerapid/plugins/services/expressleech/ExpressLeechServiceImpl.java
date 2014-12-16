package cz.vity.freerapid.plugins.services.expressleech;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class ExpressLeechServiceImpl extends AbstractFileShareService {

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    public String getName() {
        return "expressleech.com";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new ExpressLeechFileRunner();
    }

}