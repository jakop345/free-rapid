package cz.vity.freerapid.plugins.services.filecloudme;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class FileCloudmeServiceImpl extends AbstractFileShareService {

    @Override
    public String getName() {
        return "filecloud.me";
    }

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new FileCloudmeFileRunner();
    }

}