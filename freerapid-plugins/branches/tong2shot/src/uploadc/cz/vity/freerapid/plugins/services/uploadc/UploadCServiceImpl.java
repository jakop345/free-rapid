package cz.vity.freerapid.plugins.services.uploadc;

import cz.vity.freerapid.plugins.services.xfileplayer.XFilePlayerServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class UploadCServiceImpl extends XFilePlayerServiceImpl {

    @Override
    public String getServiceTitle() {
        return "UploadCore";
    }

    @Override
    public String getName() {
        return "uploadc.com";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new UploadCFileRunner();
    }

}