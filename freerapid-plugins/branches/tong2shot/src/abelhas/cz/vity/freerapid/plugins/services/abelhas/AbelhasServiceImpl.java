package cz.vity.freerapid.plugins.services.abelhas;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.hoster.PremiumAccount;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class AbelhasServiceImpl extends AbstractFileShareService {
    private static final String PLUGIN_CONFIG_FILE = "plugin_Abelhas_Account.xml";
    private volatile PremiumAccount config;

    @Override
    public String getName() {
        return "abelhas.pt";
    }

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new AbelhasFileRunner();
    }

    @Override
    public void showOptions() throws Exception {
        PremiumAccount pa = showConfigDialog();
        if (pa != null) config = pa;
    }

    PremiumAccount showConfigDialog() throws Exception {
        return showAccountDialog(getConfig(), "Abelhas.pt account details", PLUGIN_CONFIG_FILE);
    }

    PremiumAccount getConfig() throws Exception {
        synchronized (AbelhasServiceImpl.class) {
            if (config == null)
                config = getAccountConfigFromFile(PLUGIN_CONFIG_FILE);
        }
        return config;
    }

    void setConfig(final PremiumAccount config) {
        this.config = config;
    }
}