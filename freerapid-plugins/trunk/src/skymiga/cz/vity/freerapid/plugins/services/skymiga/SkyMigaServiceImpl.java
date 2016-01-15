package cz.vity.freerapid.plugins.services.skymiga;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class SkyMigaServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "SkyMiga";
    }

    @Override
    public String getName() {
        return "skymiga.com";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new SkyMigaFileRunner();
    }

}