package cz.vity.freerapid.plugins.services.fileboom;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class FileBoomServiceImpl extends AbstractFileShareService {

    @Override
    public String getName() {
        return "fileboom.me";
    }

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new FileBoomFileRunner();
    }

}