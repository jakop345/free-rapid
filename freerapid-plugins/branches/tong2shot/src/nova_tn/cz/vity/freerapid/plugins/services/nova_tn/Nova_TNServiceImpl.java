package cz.vity.freerapid.plugins.services.nova_tn;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.ConfigurationStorageSupport;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author tong2shot
 */
public class Nova_TNServiceImpl extends AbstractFileShareService {
    private static final String CONFIG_FILE = "plugin_Nova_TN.xml";
    private volatile SettingsConfig config;

    @Override
    public String getName() {
        return "nova.cz_tn";
    }

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new Nova_TNFileRunner();
    }

    @Override
    public void showOptions() throws Exception {
        super.showOptions();
        if (getPluginContext().getDialogSupport().showOKCancelDialog(new SettingsPanel(this), "Nova Televizní Noviny Settings")) {
            getPluginContext().getConfigurationStorageSupport().storeConfigToFile(config, CONFIG_FILE);
        }
    }

    SettingsConfig getConfig() throws Exception {
        synchronized (Nova_TNServiceImpl.class) {
            final ConfigurationStorageSupport storage = getPluginContext().getConfigurationStorageSupport();
            if (config == null) {
                if (!storage.configFileExists(CONFIG_FILE)) {
                    config = new SettingsConfig();
                } else {
                    config = storage.loadConfigFromFile(CONFIG_FILE, SettingsConfig.class);
                }
            }
            return config;
        }
    }

    void setConfig(final SettingsConfig config) {
        synchronized (Nova_TNServiceImpl.class) {
            this.config = config;
        }
    }

}