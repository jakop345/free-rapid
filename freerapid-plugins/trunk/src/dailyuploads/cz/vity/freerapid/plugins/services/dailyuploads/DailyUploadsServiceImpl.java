package cz.vity.freerapid.plugins.services.dailyuploads;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class DailyUploadsServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "DailyUploads";
    }

    @Override
    public String getName() {
        return "dailyuploads.net";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new DailyUploadsFileRunner();
    }

}