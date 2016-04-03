package cz.vity.freerapid.plugins.services.movdivx;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class MovDivxServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "MovDivx";
    }

    @Override
    public String getName() {
        return "movdivx.com";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new MovDivxFileRunner();
    }

}