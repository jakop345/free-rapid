package cz.vity.freerapid.plugins.services.sendfile;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class SendFileServiceImpl extends AbstractFileShareService {

    @Override
    public String getName() {
        return "sendfile.su";
    }

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new SendFileFileRunner();
    }

}