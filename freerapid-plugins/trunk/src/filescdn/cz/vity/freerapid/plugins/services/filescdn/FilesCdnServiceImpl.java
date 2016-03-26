package cz.vity.freerapid.plugins.services.filescdn;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class FilesCdnServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "FilesCdn";
    }

    @Override
    public String getName() {
        return "filescdn.com";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new FilesCdnFileRunner();
    }

}