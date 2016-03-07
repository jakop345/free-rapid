package cz.vity.freerapid.plugins.services.cloudlocker;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class CloudLockerServiceImpl extends AbstractFileShareService {

    @Override
    public String getName() {
        return "cloudlocker.biz";
    }

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new CloudLockerFileRunner();
    }

}