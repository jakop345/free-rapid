package cz.vity.freerapid.plugins.services.oload;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class OLoadServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "OLoad";
    }

    @Override
    public String getName() {
        return "oload.net";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new OLoadFileRunner();
    }

}