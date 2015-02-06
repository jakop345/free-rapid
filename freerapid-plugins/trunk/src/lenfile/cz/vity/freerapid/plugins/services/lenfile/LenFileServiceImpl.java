package cz.vity.freerapid.plugins.services.lenfile;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class LenFileServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "LenFile";
    }

    @Override
    public String getName() {
        return "lenfile.com";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new LenFileFileRunner();
    }

}