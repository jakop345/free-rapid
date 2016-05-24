package cz.vity.freerapid.plugins.services.thevideobee;

import cz.vity.freerapid.plugins.services.xfileplayer.XFilePlayerServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class TheVideoBeeServiceImpl extends XFilePlayerServiceImpl {

    @Override
    public String getServiceTitle() {
        return "TheVideoBee";
    }

    @Override
    public String getName() {
        return "thevideobee.to";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new TheVideoBeeFileRunner();
    }

}