package cz.vity.freerapid.plugins.services.wushare;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class WuShareServiceImpl extends AbstractFileShareService {

    @Override
    public String getName() {
        return "wushare.com";
    }

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new WuShareFileRunner();
    }

}