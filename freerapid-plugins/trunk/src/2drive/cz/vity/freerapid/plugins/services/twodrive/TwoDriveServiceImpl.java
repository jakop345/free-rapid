package cz.vity.freerapid.plugins.services.twodrive;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class TwoDriveServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "2Drive";
    }

    @Override
    public String getName() {
        return "2drive.net";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new TwoDriveFileRunner();
    }

}