package cz.vity.freerapid.plugins.services.filedust;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class FileDustServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "FileDust";
    }

    @Override
    public String getName() {
        return "filedust.net";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new FileDustFileRunner();
    }

}