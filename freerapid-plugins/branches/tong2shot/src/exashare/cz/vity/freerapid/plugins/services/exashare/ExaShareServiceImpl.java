package cz.vity.freerapid.plugins.services.exashare;

import cz.vity.freerapid.plugins.services.xfileplayer.XFilePlayerServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class ExaShareServiceImpl extends XFilePlayerServiceImpl {

    @Override
    public String getName() {
        return "exashare.com";
    }

    @Override
    public String getServiceTitle() {
        return "ExaShare";
    }

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new ExaShareFileRunner();
    }

}