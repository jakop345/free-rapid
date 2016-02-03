package cz.vity.freerapid.plugins.services.videopremium_tv;

import cz.vity.freerapid.plugins.services.xfileplayer.XFilePlayerServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author tong2shot
 */
public class VideoPremium_tvServiceImpl extends XFilePlayerServiceImpl {

    @Override
    public String getServiceTitle() {
        return "VideoPremium.tv";
    }

    @Override
    public String getName() {
        return "videopremium.tv";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new VideoPremium_tvFileRunner();
    }
}