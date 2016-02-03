package cz.vity.freerapid.plugins.services.uploadyourimages;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class UploadYourImagesServiceImpl extends AbstractFileShareService {

    @Override
    public String getName() {
        return "uploadyourimages.org";
    }

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new UploadYourImagesFileRunner();
    }

}