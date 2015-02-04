package cz.vity.freerapid.plugins.services.mirrorupload;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class MirrorUploadServiceImpl extends AbstractFileShareService {

    @Override
    public String getName() {
        return "mirrorupload.net";
    }

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new MirrorUploadFileRunner();
    }

}