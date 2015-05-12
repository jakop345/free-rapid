package cz.vity.freerapid.plugins.services.uploadable_premium;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.hoster.PremiumAccount;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class UploadableServiceImpl extends AbstractFileShareService {
    private static final String PLUGIN_CONFIG_FILE = "plugin_Uploadable_Premium.xml";
    private volatile PremiumAccount config;

    @Override
    public String getName() {
        return "uploadable.ch";
    }

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new UploadableFileRunner();
    }

    @Override
    public void showOptions() throws Exception {
        PremiumAccount pa = showConfigDialog();
        if (pa != null) config = pa;
    }

    PremiumAccount showConfigDialog() throws Exception {
        return showAccountDialog(getConfig(), "Uploadable.ch Premium user", PLUGIN_CONFIG_FILE);
    }

    PremiumAccount getConfig() throws Exception {
        synchronized (UploadableServiceImpl.class) {
            if (config == null)
                config = getAccountConfigFromFile(PLUGIN_CONFIG_FILE);
        }
        return config;
    }

    void setConfig(final PremiumAccount config) {
        this.config = config;
    }
}