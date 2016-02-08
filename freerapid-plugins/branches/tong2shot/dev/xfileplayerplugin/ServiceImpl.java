package cz.vity.freerapid.plugins.services.#shortsmall#;

import cz.vity.freerapid.plugins.services.xfileplayer.XFilePlayerServiceImpl;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author #author#
 */
public class #fullWithoutDot#ServiceImpl extends XFilePlayerServiceImpl {
	
	@Override
    public String getServiceTitle() {
        return "#fullWithoutDot#";
    }
	
    @Override
    public String getName() {
        return "#fulllower#";
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new #fullWithoutDot#FileRunner();
    }
}