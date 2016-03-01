package cz.vity.freerapid.plugins.services.nhk_vod;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author tong2shot
 */
public class NHK_vodServiceImpl extends AbstractFileShareService {

    @Override
    public String getName() {
        return "nhk_vod.jp";
    }

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new NHK_vodFileRunner();
    }

}