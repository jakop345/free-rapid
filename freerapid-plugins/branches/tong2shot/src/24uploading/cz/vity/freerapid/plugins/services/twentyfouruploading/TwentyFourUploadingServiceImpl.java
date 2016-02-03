package cz.vity.freerapid.plugins.services.twentyfouruploading;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class TwentyFourUploadingServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "24Uploading";
    }

    @Override
    public String getName() {
        return "24uploading.com";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new TwentyFourUploadingFileRunner();
    }

}