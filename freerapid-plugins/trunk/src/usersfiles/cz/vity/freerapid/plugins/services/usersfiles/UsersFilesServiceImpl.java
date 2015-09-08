package cz.vity.freerapid.plugins.services.usersfiles;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class UsersFilesServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "UsersFiles";
    }

    @Override
    public String getName() {
        return "usersfiles.com";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new UsersFilesFileRunner();
    }

}