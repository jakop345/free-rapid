package cz.vity.freerapid.plugins.services.megacloudfiles;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class MegaCloudFilesServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "MegaCloudFiles";
    }

    @Override
    public String getName() {
        return "megacloudfiles.com";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new MegaCloudFilesFileRunner();
    }

}