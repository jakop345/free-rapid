package cz.vity.freerapid.plugins.services.coolshare;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class CoolShareServiceImpl extends AbstractFileShareService {

    @Override
    public String getName() {
        return "coolshare.cz";
    }

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new CoolShareFileRunner();
    }

}