package cz.vity.freerapid.plugins.services.rockfile;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class RockFileServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "RockFile";
    }

    @Override
    public String getName() {
        return "rockfile.eu";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new RockFileFileRunner();
    }

}