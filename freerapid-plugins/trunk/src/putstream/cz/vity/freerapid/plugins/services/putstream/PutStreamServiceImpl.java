package cz.vity.freerapid.plugins.services.putstream;

import cz.vity.freerapid.plugins.services.xfileplayer.XFilePlayerServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class PutStreamServiceImpl extends XFilePlayerServiceImpl {

    @Override
    public String getServiceTitle() {
        return "PutStream";
    }

    @Override
    public String getName() {
        return "putstream.com";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new PutStreamFileRunner();
    }

}