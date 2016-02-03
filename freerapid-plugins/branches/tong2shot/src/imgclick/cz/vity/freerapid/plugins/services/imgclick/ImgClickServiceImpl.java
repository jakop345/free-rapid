package cz.vity.freerapid.plugins.services.imgclick;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class ImgClickServiceImpl extends AbstractFileShareService {

    @Override
    public String getName() {
        return "imgclick.net";
    }

    @Override
    public boolean supportsRunCheck() {
        return false;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new ImgClickFileRunner();
    }

}