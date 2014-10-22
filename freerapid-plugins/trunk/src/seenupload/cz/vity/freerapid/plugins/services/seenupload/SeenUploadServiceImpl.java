package cz.vity.freerapid.plugins.services.seenupload;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class SeenUploadServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "SeenUpload";
    }

    @Override
    public String getName() {
        return "seenupload.com";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new SeenUploadFileRunner();
    }

}