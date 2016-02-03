package cz.vity.freerapid.plugins.services.filehoot;

import cz.vity.freerapid.plugins.services.xfileplayer.XFilePlayerServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class FileHootServiceImpl extends XFilePlayerServiceImpl {

    @Override
    public String getServiceTitle() {
        return "FileHoot";
    }

    @Override
    public String getName() {
        return "filehoot.com";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new FileHootFileRunner();
    }

}