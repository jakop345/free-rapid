package cz.vity.freerapid.plugins.services.uncapped_downloads;

import cz.vity.freerapid.plugins.services.xfileplayer.XFilePlayerServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class Uncapped_DownloadsServiceImpl extends XFilePlayerServiceImpl {

    @Override
    public String getServiceTitle() {
        return "Uncapped-Downloads";
    }

    @Override
    public String getName() {
        return "uncapped-downloads.com";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new Uncapped_DownloadsFileRunner();
    }

}