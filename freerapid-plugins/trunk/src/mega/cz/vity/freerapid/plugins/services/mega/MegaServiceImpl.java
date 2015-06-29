package cz.vity.freerapid.plugins.services.mega;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author ntoskrnl
 */
public class MegaServiceImpl extends AbstractFileShareService {

    @Override
    public String getName() {
        return "mega.nz";
    }

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new MegaFileRunner();
    }

}