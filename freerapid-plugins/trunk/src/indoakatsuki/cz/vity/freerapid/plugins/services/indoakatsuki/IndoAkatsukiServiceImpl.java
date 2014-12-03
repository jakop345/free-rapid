package cz.vity.freerapid.plugins.services.indoakatsuki;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author tong2shot
 */
public class IndoAkatsukiServiceImpl extends AbstractFileShareService {

    @Override
    public String getName() {
        return "indoakatsuki.net";
    }

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new IndoAkatsukiFileRunner();
    }

}