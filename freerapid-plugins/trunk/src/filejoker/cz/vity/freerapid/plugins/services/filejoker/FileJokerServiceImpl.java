package cz.vity.freerapid.plugins.services.filejoker;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class FileJokerServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "FileJoker";
    }

    @Override
    public String getName() {
        return "filejoker.net";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new FileJokerFileRunner();
    }

}