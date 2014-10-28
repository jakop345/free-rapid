package cz.vity.freerapid.plugins.services.flashx;

import cz.vity.freerapid.plugins.services.xfileplayer.XFilePlayerServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author ntoskrnl
 */
public class FlashXServiceImpl extends XFilePlayerServiceImpl {

    @Override
    public String getName() {
        return "flashx.tv";
    }

    @Override
    public String getServiceTitle() {
        return "FlashX";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new FlashXFileRunner();
    }

}