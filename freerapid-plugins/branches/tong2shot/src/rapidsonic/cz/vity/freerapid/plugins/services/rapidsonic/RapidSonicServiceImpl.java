package cz.vity.freerapid.plugins.services.rapidsonic;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class RapidSonicServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "RapidSonic";
    }

    @Override
    public String getName() {
        return "rapidsonic.com";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new RapidSonicFileRunner();
    }

}