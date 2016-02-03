package cz.vity.freerapid.plugins.services.upvast;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class UpVastServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "UpVast";
    }

    @Override
    public String getName() {
        return "upvast.com";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new UpVastFileRunner();
    }

}