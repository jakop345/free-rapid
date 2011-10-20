package cz.vity.freerapid.plugins.services.filesflash;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author Heend
 */
public class FilesFlashServiceImpl extends AbstractFileShareService {

    @Override
    public String getName() {
        return "filesflash.com";
    }

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new FilesFlashRunner();
    }

}