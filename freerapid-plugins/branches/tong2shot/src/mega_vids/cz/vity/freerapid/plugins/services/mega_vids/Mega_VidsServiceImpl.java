package cz.vity.freerapid.plugins.services.mega_vids;

import cz.vity.freerapid.plugins.services.xfileplayer.XFilePlayerServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class Mega_VidsServiceImpl extends XFilePlayerServiceImpl {

    @Override
    public String getServiceTitle() {
        return "Mega_Vids";
    }

    @Override
    public String getName() {
        return "mega-vids.com";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new Mega_VidsFileRunner();
    }

}