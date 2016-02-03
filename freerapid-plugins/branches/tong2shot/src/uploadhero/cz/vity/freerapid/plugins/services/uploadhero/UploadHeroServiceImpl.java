package cz.vity.freerapid.plugins.services.uploadhero;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class UploadHeroServiceImpl extends AbstractFileShareService {

    @Override
    public String getName() {
        return "uploadhero.co";
    }

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new UploadHeroFileRunner();
    }

}