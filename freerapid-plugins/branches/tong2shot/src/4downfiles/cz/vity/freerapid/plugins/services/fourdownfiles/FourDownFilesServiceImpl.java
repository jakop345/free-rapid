package cz.vity.freerapid.plugins.services.fourdownfiles;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class FourDownFilesServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "4DownFiles";
    }

    @Override
    public String getName() {
        return "4downfiles.net";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new FourDownFilesFileRunner();
    }

}