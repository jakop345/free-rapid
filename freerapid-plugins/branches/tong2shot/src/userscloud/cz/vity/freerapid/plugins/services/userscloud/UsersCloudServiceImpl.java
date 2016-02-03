package cz.vity.freerapid.plugins.services.userscloud;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class UsersCloudServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "UsersCloud";
    }

    @Override
    public String getName() {
        return "userscloud.com";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new UsersCloudFileRunner();
    }

}