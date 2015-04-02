package cz.vity.freerapid.plugins.services.elsfile;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class ElsFileServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "ElsFile";
    }

    @Override
    public String getName() {
        return "elsfile.com";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new ElsFileFileRunner();
    }

}