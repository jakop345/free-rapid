package cz.vity.freerapid.plugins.services.sangfile;

import cz.vity.freerapid.plugins.services.xfilesharing.XFileSharingServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class SangFileServiceImpl extends XFileSharingServiceImpl {

    @Override
    public String getServiceTitle() {
        return "SangFile";
    }

    @Override
    public String getName() {
        return "sangfile.com";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new SangFileFileRunner();
    }

}