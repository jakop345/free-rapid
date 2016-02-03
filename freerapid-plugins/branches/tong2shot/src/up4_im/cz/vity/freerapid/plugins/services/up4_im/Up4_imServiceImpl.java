package cz.vity.freerapid.plugins.services.up4_im;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class Up4_imServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "Up4.im";
    }

    @Override
    public String getName() {
        return "up4.im";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new Up4_imFileRunner();
    }

}