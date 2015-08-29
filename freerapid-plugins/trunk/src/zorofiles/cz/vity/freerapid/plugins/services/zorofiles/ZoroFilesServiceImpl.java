package cz.vity.freerapid.plugins.services.zorofiles;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class ZoroFilesServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "ZoroFiles";
    }

    @Override
    public String getName() {
        return "zorofiles.com";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new ZoroFilesFileRunner();
    }

}