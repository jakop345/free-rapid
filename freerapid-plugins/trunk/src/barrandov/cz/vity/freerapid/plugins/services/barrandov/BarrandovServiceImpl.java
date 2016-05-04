package cz.vity.freerapid.plugins.services.barrandov;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.hoster.PremiumAccount;
import cz.vity.freerapid.plugins.webclient.interfaces.ConfigurationStorageSupport;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 * @author JPEXS
 */
public class BarrandovServiceImpl extends AbstractFileShareService {
    private static final String CONFIG_FILE = "BarrandovSettings.xml";
    private static final String PREMIUM_FILE = "BarrandovPremium.xml";
    private volatile BarrandovSettingsConfig config;
    private volatile PremiumAccount account;

    public String getName() {
        return "barrandov.tv";
    }

    @Override
    public boolean supportsRunCheck() {
        return true;//ok
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new BarrandovFileRunner();
    }

    @Override
    public void showOptions() throws Exception {
        super.showOptions();

        if (getPluginContext().getDialogSupport().showOKCancelDialog(new BarrandovSettingsPanel(this), "Barrandov.tv settings")) {
            getPluginContext().getConfigurationStorageSupport().storeConfigToFile(config, CONFIG_FILE);
        }
    }

    void setConfig(BarrandovSettingsConfig config) {
        this.config = config;
    }

    public BarrandovSettingsConfig getConfig() throws Exception {
        synchronized (BarrandovServiceImpl.class) {
            final ConfigurationStorageSupport storage = getPluginContext().getConfigurationStorageSupport();
            if (config == null) {
                if (!storage.configFileExists(CONFIG_FILE)) {
                    config = new BarrandovSettingsConfig();
                    config.setQualitySetting(1);
                } else {
                    config = storage.loadConfigFromFile(CONFIG_FILE, BarrandovSettingsConfig.class);
                }
            }
        }
        return config;
    }

    public void showAccount() {
        PremiumAccount pa = showAccountDialog(getAccount(), "Barrandov.tv", PREMIUM_FILE);
        if (pa != null) setAccount(pa);
    }

    public PremiumAccount getAccount() {
        synchronized (BarrandovServiceImpl.class) {
            if (account == null) {
                account = getAccountConfigFromFile(PREMIUM_FILE);
            }
        }
        return account;
    }

    public void setAccount(PremiumAccount account) {
        this.account = account;
    }

}