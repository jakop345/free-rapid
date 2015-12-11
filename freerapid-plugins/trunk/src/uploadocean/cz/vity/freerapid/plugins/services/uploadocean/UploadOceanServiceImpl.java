package cz.vity.freerapid.plugins.services.uploadocean;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class UploadOceanServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "UploadOcean";
    }

    @Override
    public String getName() {
        return "uploadocean.com";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new UploadOceanFileRunner();
    }

}