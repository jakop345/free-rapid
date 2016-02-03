package cz.vity.freerapid.plugins.services.imgmega;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class ImgMegaServiceImpl extends AbstractFileShareService {

    @Override
    public String getName() {
        return "imgmega.com";
    }

    @Override
    public boolean supportsRunCheck() {
        return false;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new ImgMegaFileRunner();
    }

}