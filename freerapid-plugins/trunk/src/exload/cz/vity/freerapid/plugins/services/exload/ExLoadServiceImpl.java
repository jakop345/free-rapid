package cz.vity.freerapid.plugins.services.exload;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class ExLoadServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "Ex-Load";
    }

    @Override
    public String getName() {
        return "ex-load.com";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new ExLoadFileRunner();
    }

}