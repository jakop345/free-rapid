package cz.vity.freerapid.plugins.services.imageweb;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class ImageWebServiceImpl extends AbstractFileShareService {

    @Override
    public String getName() {
        return "imageweb.ws";
    }

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new ImageWebFileRunner();
    }

}