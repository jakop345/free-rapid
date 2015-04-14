package cz.vity.freerapid.plugins.services.ehentai;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class EHentaiServiceImpl extends AbstractFileShareService {

    @Override
    public String getName() {
        return "e-hentai.org";
    }

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new EHentaiFileRunner();
    }

}