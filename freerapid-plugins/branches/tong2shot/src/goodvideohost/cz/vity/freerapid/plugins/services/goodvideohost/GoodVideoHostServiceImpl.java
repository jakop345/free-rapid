package cz.vity.freerapid.plugins.services.goodvideohost;

import cz.vity.freerapid.plugins.services.xfileplayer.XFilePlayerServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class GoodVideoHostServiceImpl extends XFilePlayerServiceImpl {

    @Override
    public String getServiceTitle() {
        return "GoodVideoHost";
    }

    @Override
    public String getName() {
        return "goodvideohost.com";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new GoodVideoHostFileRunner();
    }

}