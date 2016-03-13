package cz.vity.freerapid.plugins.services.uptostream;

import cz.vity.freerapid.plugins.webclient.AbstractFileShareService;
import cz.vity.freerapid.plugins.webclient.interfaces.ConfigurationStorageSupport;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;

/**
 * Class that provides basic info about plugin
 *
 * @author tong2shot
 */
public class UpToStreamServiceImpl extends AbstractFileShareService {
    private static final String CONFIG_FILE = "plugin_UpToStream.xml";
    private volatile SettingsConfig config;

    @Override
    public String getName() {
        return "uptostream.com";
    }

    @Override
    public boolean supportsRunCheck() {
        return true;
    }

    @Override
    protected PluginRunner getPluginRunnerInstance() {
        return new UpToStreamFileRunner();
    }

    @Override
    public void showOptions() throws Exception {
        super.showOptions();
        if (getPluginContext().getDialogSupport().showOKCancelDialog(new SettingsPanel(this), "UpToStream settings")) {
            getPluginContext().getConfigurationStorageSupport().storeConfigToFile(config, CONFIG_FILE);
        }
    }

    SettingsConfig getConfig() throws Exception {
        synchronized (UpToStreamServiceImpl.class) {
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
    }

    void setConfig(final SettingsConfig config) {
        synchronized (UpToStreamServiceImpl.class) {
            this.config = config;
        }
    }

}