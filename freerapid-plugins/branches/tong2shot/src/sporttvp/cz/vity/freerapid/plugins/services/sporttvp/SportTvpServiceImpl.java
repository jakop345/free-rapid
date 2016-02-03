package cz.vity.freerapid.plugins.services.sporttvp;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.ConfigurationStorageSupport;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author birchie
 */
public class SportTvpServiceImpl extends AbstractFileShareService {
    private static final String CONFIG_FILE = "plugin_SportTvpPlSettings.xml";
    private volatile SettingsConfig config;

    @Override
    public String getName() {
        return "sport.tvp.pl";
    }

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new SportTvpFileRunner();
    }

    @Override
    public void showOptions() throws Exception {
        super.showOptions();
        if (getPluginContext().getDialogSupport().showOKCancelDialog(new SettingsPanel(this), "sport.tvp.pl settings")) {
            getPluginContext().getConfigurationStorageSupport().storeConfigToFile(config, CONFIG_FILE);
        }
    }

    public SettingsConfig getConfig() throws Exception {
        final ConfigurationStorageSupport storage = getPluginContext().getConfigurationStorageSupport();
        if (config == null) {
            if (!storage.configFileExists(CONFIG_FILE)) {
                config = new SettingsConfig();
            } else {
                try {
                    config = storage.loadConfigFromFile(CONFIG_FILE, SettingsConfig.class);
                } catch (Exception e) {
                    config = new SettingsConfig();
                }
            }
        }
        return config;
    }

    public void setConfig(final SettingsConfig config) {
        synchronized (SportTvpServiceImpl.class) {
            this.config = config;
        }
    }

}