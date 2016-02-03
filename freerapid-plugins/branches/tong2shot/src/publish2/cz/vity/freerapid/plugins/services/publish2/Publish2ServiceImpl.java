package cz.vity.freerapid.plugins.services.publish2;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class Publish2ServiceImpl extends AbstractFileShareService {

    @Override
    public String getName() {
        return "publish2.me";
    }

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new Publish2FileRunner();
    }

}