package cz.vity.freerapid.plugins.services.timeload;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class TimeLoadServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "TimeLoad";
    }

    @Override
    public String getName() {
        return "timeload.net";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new TimeLoadFileRunner();
    }

}